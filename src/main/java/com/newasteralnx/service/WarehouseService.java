package com.newasteralnx.service;

import com.newasteralnx.constant.WarehouseStatus;
import com.newasteralnx.dto.request.WarehouseRequest;
import com.newasteralnx.dto.response.WarehouseResponse;
import com.newasteralnx.entity.Warehouse;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.constant.ShipmentStatus;
import com.newasteralnx.repository.CommodityRepository;
import com.newasteralnx.repository.ShipmentRepository;
import com.newasteralnx.repository.WarehouseRepository;

import java.util.EnumSet;
import com.newasteralnx.util.SecurityUtils;
import com.newasteralnx.util.SequentialCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final CommodityRepository commodityRepository;
    private final ShipmentRepository shipmentRepository;

    public WarehouseResponse create(WarehouseRequest request) {
        String name = request.getName().trim();
        if (warehouseRepository.existsByNameIgnoreCaseAndDeletedFalse(name)) {
            throw new BusinessException("Warehouse name must be unique");
        }

        Warehouse warehouse = Warehouse.builder()
                .warehouseCode(SequentialCodes.next("WH", warehouseRepository.count() + 1, warehouseRepository::existsByWarehouseCode))
                .name(name)
                .address(request.getAddress().trim())
                .city(request.getCity().trim())
                .state(request.getState().trim())
                .country(request.getCountry().trim())
                .postalCode(request.getPostalCode().trim())
                .managerName(request.getManagerName())
                .capacity(request.getCapacity())
                .availableCapacity(request.getCapacity())
                .status(WarehouseStatus.ACTIVE)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toResponse(warehouseRepository.save(warehouse));
    }

    @Transactional(readOnly = true)
    public Page<WarehouseResponse> list(Pageable pageable) {
        return warehouseRepository.findByDeletedFalse(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public WarehouseResponse get(Long id) {
        return toResponse(requireExisting(id));
    }

    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse warehouse = requireExisting(id);
        String name = request.getName().trim();
        if (warehouseRepository.existsByNameIgnoreCaseAndDeletedFalseAndIdNot(name, id)) {
            throw new BusinessException("Warehouse name must be unique");
        }

        BigDecimal occupied = occupied(warehouse);
        if (request.getCapacity().compareTo(occupied) < 0) {
            throw new BusinessException("Warehouse capacity cannot be reduced below currently occupied capacity");
        }

        BigDecimal newAvailable = request.getCapacity().subtract(occupied);
        warehouse.setName(name);
        warehouse.setAddress(request.getAddress().trim());
        warehouse.setCity(request.getCity().trim());
        warehouse.setState(request.getState().trim());
        warehouse.setCountry(request.getCountry().trim());
        warehouse.setPostalCode(request.getPostalCode().trim());
        warehouse.setManagerName(request.getManagerName());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setAvailableCapacity(newAvailable);
        warehouse.setUpdatedAt(LocalDateTime.now());
        return toResponse(warehouseRepository.save(warehouse));
    }

    public void delete(Long id) {
        Warehouse warehouse = requireExisting(id);
        if (commodityRepository.existsByWarehouseId(id)) {
            throw new BusinessException(
                    "Cannot delete warehouse while commodities/stock exist. Transfer or clear stock first.");
        }
        if (shipmentRepository.existsByWarehouseIdAndStatusNotIn(id,
                EnumSet.of(ShipmentStatus.DELIVERED, ShipmentStatus.CANCELLED))) {
            throw new BusinessException(
                    "Cannot delete warehouse while open shipments are linked. Complete or cancel shipments first.");
        }
        warehouse.setDeleted(true);
        warehouse.setStatus(WarehouseStatus.INACTIVE);
        warehouse.setDeletedAt(LocalDateTime.now());
        warehouse.setDeletedBy(SecurityUtils.requireCurrentUserId());
        warehouse.setUpdatedAt(LocalDateTime.now());
        warehouseRepository.save(warehouse);
    }

    public WarehouseResponse updateStatus(Long id, WarehouseStatus status) {
        Warehouse warehouse = requireExisting(id);
        warehouse.setStatus(status);
        warehouse.setUpdatedAt(LocalDateTime.now());
        return toResponse(warehouseRepository.save(warehouse));
    }

    public Warehouse requireActive(Long id) {
        Warehouse warehouse = requireExisting(id);
        if (warehouse.getStatus() != WarehouseStatus.ACTIVE) {
            throw new BusinessException("Inactive warehouse cannot receive new inventory or bookings");
        }
        return warehouse;
    }

    public Warehouse requireExisting(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        if (Boolean.TRUE.equals(warehouse.getDeleted())) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + id);
        }
        return warehouse;
    }

    public void consumeCapacity(Warehouse warehouse, BigDecimal volumeOrQuantity) {
        BigDecimal next = warehouse.getAvailableCapacity().subtract(volumeOrQuantity);
        if (next.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Insufficient warehouse capacity");
        }
        warehouse.setAvailableCapacity(next);
    }

    public void restoreCapacity(Warehouse warehouse, BigDecimal volumeOrQuantity) {
        BigDecimal next = warehouse.getAvailableCapacity().add(volumeOrQuantity);
        if (next.compareTo(warehouse.getCapacity()) > 0) {
            next = warehouse.getCapacity();
        }
        warehouse.setAvailableCapacity(next);
    }

    private BigDecimal occupied(Warehouse warehouse) {
        return warehouse.getCapacity().subtract(warehouse.getAvailableCapacity());
    }

    private WarehouseResponse toResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .warehouseCode(warehouse.getWarehouseCode())
                .name(warehouse.getName())
                .address(warehouse.getAddress())
                .city(warehouse.getCity())
                .state(warehouse.getState())
                .country(warehouse.getCountry())
                .postalCode(warehouse.getPostalCode())
                .managerName(warehouse.getManagerName())
                .capacity(warehouse.getCapacity())
                .availableCapacity(warehouse.getAvailableCapacity())
                .occupiedCapacity(occupied(warehouse))
                .status(warehouse.getStatus())
                .createdAt(warehouse.getCreatedAt())
                .build();
    }
}
