package com.newasteralnx.service;

import com.newasteralnx.constant.ShipmentStatus;
import com.newasteralnx.constant.TripStatus;
import com.newasteralnx.dto.request.ProofOfDeliveryRequest;
import com.newasteralnx.dto.request.ShipmentRequest;
import com.newasteralnx.dto.response.ShipmentResponse;
import com.newasteralnx.entity.*;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.ProofOfDeliveryRepository;
import com.newasteralnx.repository.ShipmentRepository;
import com.newasteralnx.repository.TripRepository;
import com.newasteralnx.util.SecurityUtils;
import com.newasteralnx.util.SequentialCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final TripRepository tripRepository;
    private final ProofOfDeliveryRepository proofOfDeliveryRepository;
    private final OrderService orderService;
    private final WarehouseService warehouseService;
    private final DriverService driverService;
    private final VehicleService vehicleService;
    private final BookingService bookingService;
    private final CommodityService commodityService;
    private final CustomerService customerService;

    public ShipmentResponse create(ShipmentRequest request) {
        LogisticsOrder order = orderService.requireShippable(request.getOrderId());
        Customer customer = customerService.requireActiveForTransaction(order.getCustomer().getId());

        Warehouse warehouse = warehouseService.requireActive(request.getWarehouseId());
        Driver driver = driverService.requireAssignable(request.getDriverId());
        Vehicle vehicle = vehicleService.requireAssignable(request.getVehicleId());
        Booking booking = request.getBookingId() == null ? null : bookingService.require(request.getBookingId());
        Commodity commodity = commodityService.requireInActiveWarehouse(request.getCommodityId());
        if (commodity.getWarehouse() == null || !commodity.getWarehouse().getId().equals(warehouse.getId())) {
            throw new BusinessException("Selected commodity does not belong to the selected warehouse");
        }
        if (request.getExpectedDelivery() == null) throw new BusinessException("Expected delivery date is required");
        if (request.getQuantity() == null || request.getQuantity().signum() <= 0) throw new BusinessException("Shipment quantity must be greater than zero");
        if (shipmentRepository.existsByOrderIdAndDriverIdAndStatusNotIn(order.getId(), driver.getId(), java.util.List.of(ShipmentStatus.CANCELLED))) {
            throw new BusinessException("This driver is already assigned to a shipment for this order.");
        }
        commodityService.decreaseQuantity(commodity, request.getQuantity());

        Shipment shipment = Shipment.builder()
                .shipmentNumber(SequentialCodes.next("SHP", shipmentRepository.count() + 1, shipmentRepository::existsByShipmentNumber))
                .order(order)
                .customer(customer)
                .origin(order.getOrigin())
                .destination(order.getDestination())
                .warehouse(warehouse)
                .driver(driver)
                .vehicle(vehicle)
                .booking(booking)
                .commodity(commodity)
                .quantity(request.getQuantity() == null ? order.getQuantity() : request.getQuantity())
                .status(ShipmentStatus.CREATED)
                .shipmentDate(request.getShipmentDate() == null ? LocalDate.now() : request.getShipmentDate())
                .expectedDelivery(request.getExpectedDelivery())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Shipment saved = shipmentRepository.save(shipment);
        if (driver != null && vehicle != null) {
            createTrip(saved, driver, vehicle);
        }
        orderService.markShipped(order);
        log.info("Shipment created shipmentNumber={}", saved.getShipmentNumber());
        return toResponse(saved);
    }

    public ShipmentResponse assign(Long id, Long driverId, Long vehicleId) {
        Shipment shipment = require(id);
        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new BusinessException("Delivered shipment cannot be modified");
        }
        if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
            throw new BusinessException("Cancelled shipment cannot be restarted");
        }
        if (driverId != null) {
            if (shipmentRepository.existsByOrderIdAndDriverIdAndStatusNotIn(shipment.getOrder().getId(), driverId, java.util.List.of(ShipmentStatus.CANCELLED))
                    && (shipment.getDriver() == null || !shipment.getDriver().getId().equals(driverId))) {
                throw new BusinessException("This driver is already assigned to a shipment for this order.");
            }
            shipment.setDriver(driverService.requireAssignable(driverId));
        }
        if (vehicleId != null) {
            shipment.setVehicle(vehicleService.requireAssignable(vehicleId));
        }
        shipment.setUpdatedAt(LocalDateTime.now());
        Shipment saved = shipmentRepository.save(shipment);
        if (saved.getDriver() != null && saved.getVehicle() != null
                && tripRepository.findByShipmentId(saved.getId()).isEmpty()) {
            createTrip(saved, saved.getDriver(), saved.getVehicle());
        }
        return toResponse(saved);
    }

    public ShipmentResponse dispatch(Long id) {
        Shipment shipment = require(id);
        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new BusinessException("Delivered shipment cannot be modified");
        }
        if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
            throw new BusinessException("Cancelled shipment cannot be restarted");
        }
        if (shipment.getDriver() == null) {
            throw new BusinessException("Shipment requires assigned driver before dispatch");
        }
        if (shipment.getVehicle() == null) {
            throw new BusinessException("Shipment requires assigned vehicle before dispatch");
        }
        driverService.requireExisting(shipment.getDriver().getId());
        vehicleService.requireAssignable(shipment.getVehicle().getId());
        if (tripRepository.findByShipmentId(shipment.getId()).isEmpty()) {
            createTrip(shipment, shipment.getDriver(), shipment.getVehicle());
        }
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setUpdatedAt(LocalDateTime.now());
        return toResponse(shipmentRepository.save(shipment));
    }

    public ShipmentResponse updateStatus(Long id, ShipmentStatus status) {
        Shipment shipment = require(id);
        assertAllowedShipmentTransition(shipment.getStatus(), status);
        shipment.setStatus(status);
        if (status == ShipmentStatus.DELIVERED) {
            completeDelivery(shipment, null);
        }
        if (status == ShipmentStatus.CANCELLED) {
            restoreStockOnCancel(shipment);
        }
        if (status == ShipmentStatus.IN_TRANSIT || status == ShipmentStatus.PICKED_UP) {
            tripRepository.findByShipmentId(id).ifPresent(trip -> {
                if (trip.getStartTime() == null) {
                    trip.setStartTime(LocalDateTime.now());
                    trip.setStatus(TripStatus.STARTED);
                    trip.setUpdatedAt(LocalDateTime.now());
                }
            });
        }
        shipment.setUpdatedAt(LocalDateTime.now());
        return toResponse(shipmentRepository.save(shipment));
    }

    public ShipmentResponse cancel(Long id) {
        Shipment shipment = require(id);
        if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
            throw new BusinessException("Shipment is already cancelled");
        }
        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new BusinessException("Delivered shipment cannot be cancelled");
        }
        shipment.setStatus(ShipmentStatus.CANCELLED);
        restoreStockOnCancel(shipment);
        tripRepository.findByShipmentId(id).ifPresent(trip -> {
            if (trip.getStatus() != TripStatus.COMPLETED) {
                trip.setStatus(TripStatus.CANCELLED);
                trip.setUpdatedAt(LocalDateTime.now());
            }
        });
        shipment.setUpdatedAt(LocalDateTime.now());
        log.info("Shipment cancelled shipmentNumber={}", shipment.getShipmentNumber());
        return toResponse(shipmentRepository.save(shipment));
    }

    /**
     * Mark shipment delivered and record Proof of Delivery.
     */
    public ShipmentResponse deliver(Long id, ProofOfDeliveryRequest request) {
        Shipment shipment = require(id);
        if (SecurityUtils.hasRole("DRIVER")) {
            Driver currentDriver = driverService.requireCurrentDriverProfile();
            if (shipment.getDriver() == null || !shipment.getDriver().getId().equals(currentDriver.getId())) {
                throw new BusinessException("Only the driver assigned to this shipment can complete its delivery");
            }
        }
        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new BusinessException("Shipment is already delivered");
        }
        if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
            throw new BusinessException("Cancelled shipment cannot be delivered");
        }
        if (shipment.getStatus() == ShipmentStatus.CREATED || shipment.getStatus() == ShipmentStatus.READY_FOR_PICKUP) {
            throw new BusinessException("Shipment must be dispatched / in transit before delivery");
        }
        shipment.setStatus(ShipmentStatus.DELIVERED);
        completeDelivery(shipment, request);
        shipment.setUpdatedAt(LocalDateTime.now());
        return toResponse(shipmentRepository.save(shipment));
    }

    private void completeDelivery(Shipment shipment, ProofOfDeliveryRequest request) {
        LocalDateTime now = LocalDateTime.now();
        shipment.setActualDelivery(LocalDate.now());
        orderService.markDelivered(shipment.getOrder());
        tripRepository.findByShipmentId(shipment.getId()).ifPresent(trip -> {
            trip.setStatus(TripStatus.COMPLETED);
            trip.setEndTime(now);
            if (trip.getStartTime() != null) {
                trip.setDurationMinutes(Duration.between(trip.getStartTime(), now).toMinutes());
            }
            trip.setUpdatedAt(now);
        });
        if (!proofOfDeliveryRepository.existsByShipmentId(shipment.getId())) {
            String receivedBy = request != null && request.getReceivedBy() != null
                    ? request.getReceivedBy().trim() : "Recipient";
            String remarks = request != null ? request.getRemarks() : null;
            String outcome = request != null && request.getOutcome() != null
                    ? request.getOutcome().trim() : "DELIVERED";
            ProofOfDelivery pod = ProofOfDelivery.builder()
                    .shipment(shipment)
                    .receivedBy(receivedBy)
                    .remarks(remarks)
                    .outcome(outcome)
                    .deliveredAt(now)
                    .recordedBy(SecurityUtils.requireCurrentUserId())
                    .createdAt(now)
                    .build();
            proofOfDeliveryRepository.save(pod);
            log.info("POD recorded shipmentNumber={} receivedBy={}", shipment.getShipmentNumber(), receivedBy);
        }
    }

    private void restoreStockOnCancel(Shipment shipment) {
        if (shipment.getCommodity() != null && shipment.getQuantity() != null) {
            commodityService.increaseQuantity(shipment.getCommodity(), shipment.getQuantity());
            if (shipment.getWarehouse() != null) {
                var occupy = CommodityService.occupyAmount(
                        shipment.getCommodity().getVolume(), shipment.getQuantity());
                warehouseService.restoreCapacity(shipment.getWarehouse(), occupy);
            }
            log.info("Stock restored after cancel shipmentNumber={}", shipment.getShipmentNumber());
        }
    }

    /**
     * CREATED → READY_FOR_PICKUP | PICKED_UP | IN_TRANSIT | CANCELLED
     * READY_FOR_PICKUP → PICKED_UP | IN_TRANSIT | DELAYED | CANCELLED
     * PICKED_UP → IN_TRANSIT | DELAYED | CANCELLED
     * IN_TRANSIT → ARRIVING | DELAYED | DELIVERED | CANCELLED
     * ARRIVING → DELIVERED | DELAYED | CANCELLED
     * DELAYED → IN_TRANSIT | ARRIVING | DELIVERED | CANCELLED
     * DELIVERED / CANCELLED → terminal
     */
    private void assertAllowedShipmentTransition(ShipmentStatus current, ShipmentStatus target) {
        if (current == target) {
            return;
        }
        if (current == ShipmentStatus.DELIVERED) {
            throw new BusinessException("Delivered shipment cannot be modified");
        }
        if (current == ShipmentStatus.CANCELLED) {
            throw new BusinessException("Cancelled shipment cannot be restarted");
        }
        boolean allowed = switch (current) {
            case CREATED -> target == ShipmentStatus.READY_FOR_PICKUP
                    || target == ShipmentStatus.PICKED_UP
                    || target == ShipmentStatus.IN_TRANSIT
                    || target == ShipmentStatus.CANCELLED;
            case READY_FOR_PICKUP -> target == ShipmentStatus.PICKED_UP
                    || target == ShipmentStatus.IN_TRANSIT
                    || target == ShipmentStatus.DELAYED
                    || target == ShipmentStatus.CANCELLED;
            case PICKED_UP -> target == ShipmentStatus.IN_TRANSIT
                    || target == ShipmentStatus.DELAYED
                    || target == ShipmentStatus.CANCELLED;
            case IN_TRANSIT -> target == ShipmentStatus.ARRIVING
                    || target == ShipmentStatus.DELAYED
                    || target == ShipmentStatus.DELIVERED
                    || target == ShipmentStatus.CANCELLED;
            case ARRIVING -> target == ShipmentStatus.DELIVERED
                    || target == ShipmentStatus.DELAYED
                    || target == ShipmentStatus.CANCELLED;
            case DELAYED -> target == ShipmentStatus.IN_TRANSIT
                    || target == ShipmentStatus.ARRIVING
                    || target == ShipmentStatus.DELIVERED
                    || target == ShipmentStatus.CANCELLED;
            default -> false;
        };
        if (!allowed) {
            throw new BusinessException(
                    "Invalid shipment status transition from " + current + " to " + target);
        }
    }

    @Transactional(readOnly = true)
    public Page<ShipmentResponse> list(Pageable pageable) {
        return shipmentRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse get(Long id) {
        return toResponse(require(id));
    }

    @Transactional(readOnly = true)
    public Page<ShipmentResponse> mine(Pageable pageable) {
        Driver driver = driverService.requireCurrentDriverProfile();
        return shipmentRepository.findByDriverIdAndStatus(driver.getId(), ShipmentStatus.DELIVERED, pageable).map(this::toResponse);
    }

    public Shipment require(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
    }

    private void createTrip(Shipment shipment, Driver driver, Vehicle vehicle) {
        Trip trip = Trip.builder()
                .tripNumber(SequentialCodes.next("TRP", tripRepository.count() + 1, tripRepository::existsByTripNumber))
                .shipment(shipment)
                .driver(driver)
                .vehicle(vehicle)
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .status(TripStatus.ASSIGNED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        tripRepository.save(trip);
    }

    private ShipmentResponse toResponse(Shipment shipment) {
        return ShipmentResponse.builder()
                .id(shipment.getId())
                .shipmentNumber(shipment.getShipmentNumber())
                .orderId(shipment.getOrder().getId())
                .customerId(shipment.getCustomer().getId())
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .warehouseId(shipment.getWarehouse() == null ? null : shipment.getWarehouse().getId())
                .driverId(shipment.getDriver() == null ? null : shipment.getDriver().getId())
                .vehicleId(shipment.getVehicle() == null ? null : shipment.getVehicle().getId())
                .bookingId(shipment.getBooking() == null ? null : shipment.getBooking().getId())
                .commodityId(shipment.getCommodity() == null ? null : shipment.getCommodity().getId())
                .quantity(shipment.getQuantity())
                .status(shipment.getStatus())
                .shipmentDate(shipment.getShipmentDate())
                .expectedDelivery(shipment.getExpectedDelivery())
                .actualDelivery(shipment.getActualDelivery())
                .build();
    }
}
