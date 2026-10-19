package com.newasteralnx.service;

import com.newasteralnx.constant.BookingStatus;
import com.newasteralnx.dto.request.AssignmentRequest;
import com.newasteralnx.dto.request.BookingRequest;
import com.newasteralnx.dto.response.BookingResponse;
import com.newasteralnx.entity.Booking;
import com.newasteralnx.entity.Customer;
import com.newasteralnx.entity.Driver;
import com.newasteralnx.entity.LogisticsOrder;
import com.newasteralnx.entity.Vehicle;
import com.newasteralnx.entity.Warehouse;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.BookingRepository;
import com.newasteralnx.util.SequentialCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final WarehouseService warehouseService;
    private final VehicleService vehicleService;
    private final DriverService driverService;

    public BookingResponse create(BookingRequest request) {
        Customer customer = customerService.requireActiveForTransaction(request.getCustomerId());
        LogisticsOrder order = orderService.require(request.getOrderId());
        if (bookingRepository.existsByOrderId(order.getId())) {
            throw new BusinessException("This order already has a booking. Select an order that has not been booked.");
        }
        if (order.getStatus() == com.newasteralnx.constant.OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot create booking for a cancelled order");
        }
        if (order.getStatus() == com.newasteralnx.constant.OrderStatus.DRAFT) {
            throw new BusinessException("Confirm the order before creating a booking");
        }
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("Order does not belong to the selected customer");
        }
        Warehouse warehouse = request.getWarehouseId() == null ? null : warehouseService.requireActive(request.getWarehouseId());
        Vehicle vehicle = request.getVehicleId() == null ? null : vehicleService.requireAssignable(request.getVehicleId());
        Driver driver = request.getDriverId() == null ? null : driverService.requireAssignable(request.getDriverId());

        BookingStatus status = (vehicle != null && driver != null) ? BookingStatus.ASSIGNED : BookingStatus.PENDING;

        Booking booking = Booking.builder()
                .bookingNumber(SequentialCodes.next("BKG", bookingRepository.count() + 1, bookingRepository::existsByBookingNumber))
                .customer(customer)
                .order(order)
                .warehouse(warehouse)
                .pickup(request.getPickup().trim())
                .destination(request.getDestination().trim())
                .vehicle(vehicle)
                .driver(driver)
                .scheduledDate(request.getScheduledDate())
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingResponse get(Long id) {
        return toResponse(require(id));
    }

    public BookingResponse assign(Long id, AssignmentRequest request) {
        Booking booking = require(id);
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BusinessException("Completed or cancelled bookings cannot be assigned");
        }
        if (request.getVehicleId() != null) {
            booking.setVehicle(vehicleService.requireAssignable(request.getVehicleId()));
        }
        if (request.getDriverId() != null) {
            booking.setDriver(driverService.requireAssignable(request.getDriverId()));
        }
        if (booking.getVehicle() != null && booking.getDriver() != null) {
            booking.setStatus(BookingStatus.ASSIGNED);
        }
        booking.setUpdatedAt(LocalDateTime.now());
        return toResponse(bookingRepository.save(booking));
    }

    public BookingResponse updateStatus(Long id, BookingStatus status) {
        Booking booking = require(id);
        BookingStatus current = booking.getStatus();
        if (current == status) {
            return toResponse(booking);
        }
        if (current == BookingStatus.COMPLETED || current == BookingStatus.CANCELLED) {
            throw new BusinessException("Completed or cancelled booking cannot change status");
        }
        boolean allowed = switch (current) {
            case PENDING -> status == BookingStatus.CONFIRMED
                    || status == BookingStatus.ASSIGNED
                    || status == BookingStatus.CANCELLED;
            case CONFIRMED -> status == BookingStatus.ASSIGNED
                    || status == BookingStatus.IN_PROGRESS
                    || status == BookingStatus.CANCELLED;
            case ASSIGNED -> status == BookingStatus.IN_PROGRESS
                    || status == BookingStatus.COMPLETED
                    || status == BookingStatus.CANCELLED;
            case IN_PROGRESS -> status == BookingStatus.COMPLETED || status == BookingStatus.CANCELLED;
            default -> false;
        };
        if (!allowed) {
            throw new BusinessException("Invalid booking status transition from " + current + " to " + status);
        }
        booking.setStatus(status);
        booking.setUpdatedAt(LocalDateTime.now());
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> list(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(this::toResponse);
    }

    public Booking require(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingNumber(booking.getBookingNumber())
                .customerId(booking.getCustomer().getId())
                .orderId(booking.getOrder().getId())
                .orderNumber(booking.getOrder().getOrderNumber())
                .createdAt(booking.getCreatedAt())
                .warehouseId(booking.getWarehouse() == null ? null : booking.getWarehouse().getId())
                .pickup(booking.getPickup())
                .destination(booking.getDestination())
                .vehicleId(booking.getVehicle() == null ? null : booking.getVehicle().getId())
                .driverId(booking.getDriver() == null ? null : booking.getDriver().getId())
                .scheduledDate(booking.getScheduledDate())
                .status(booking.getStatus())
                .build();
    }
}
