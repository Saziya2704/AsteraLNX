package com.newasteralnx.service;

import com.newasteralnx.constant.CommodityStatus;
import com.newasteralnx.dto.request.CommodityRequest;
import com.newasteralnx.dto.response.CommodityResponse;
import com.newasteralnx.entity.Commodity;
import com.newasteralnx.entity.Warehouse;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.constant.ShipmentStatus;
import com.newasteralnx.repository.CommodityRepository;
import com.newasteralnx.repository.ShipmentRepository;

import java.util.EnumSet;
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
public class CommodityService {

    private final CommodityRepository commodityRepository;
    private final WarehouseService warehouseService;
    private final ShipmentRepository shipmentRepository;

    public CommodityResponse create(CommodityRequest request) {
        Warehouse warehouse = warehouseService.requireActive(request.getWarehouseId());
        BigDecimal occupy = occupyAmount(request.getVolume(), request.getQuantity());
        warehouseService.consumeCapacity(warehouse, occupy);

        Commodity commodity = Commodity.builder()
                .commodityCode(SequentialCodes.next("COM", commodityRepository.count() + 1, commodityRepository::existsByCommodityCode))
                .name(request.getName().trim())
                .category(request.getCategory())
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .unit(request.getUnit().trim())
                .weight(request.getWeight() == null ? BigDecimal.ZERO : request.getWeight())
                .volume(request.getVolume() == null ? BigDecimal.ZERO : request.getVolume())
                .warehouse(warehouse)
                .storageLocation(request.getStorageLocation())
                .status(CommodityStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toResponse(commodityRepository.save(commodity));
    }

    @Transactional(readOnly = true)
    public Page<CommodityResponse> list(Long warehouseId, Pageable pageable) {
        if (warehouseId != null) {
            return commodityRepository.findByWarehouseId(warehouseId, pageable).map(this::toResponse);
        }
        return commodityRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CommodityResponse get(Long id) {
        return toResponse(require(id));
    }

    public CommodityResponse update(Long id, CommodityRequest request) {
        Commodity commodity = require(id);
        Warehouse warehouse = warehouseService.requireActive(request.getWarehouseId());
        BigDecimal oldOccupy = occupyAmount(commodity.getVolume(), commodity.getQuantity());
        BigDecimal newOccupy = occupyAmount(request.getVolume(), request.getQuantity());

        if (!commodity.getWarehouse().getId().equals(warehouse.getId())) {
            warehouseService.restoreCapacity(commodity.getWarehouse(), oldOccupy);
            warehouseService.consumeCapacity(warehouse, newOccupy);
            commodity.setWarehouse(warehouse);
        } else {
            BigDecimal delta = newOccupy.subtract(oldOccupy);
            if (delta.compareTo(BigDecimal.ZERO) > 0) {
                warehouseService.consumeCapacity(warehouse, delta);
            } else if (delta.compareTo(BigDecimal.ZERO) < 0) {
                warehouseService.restoreCapacity(warehouse, delta.abs());
            }
        }

        commodity.setName(request.getName().trim());
        commodity.setCategory(request.getCategory());
        commodity.setDescription(request.getDescription());
        commodity.setQuantity(request.getQuantity());
        commodity.setUnit(request.getUnit().trim());
        commodity.setWeight(request.getWeight() == null ? BigDecimal.ZERO : request.getWeight());
        commodity.setVolume(request.getVolume() == null ? BigDecimal.ZERO : request.getVolume());
        commodity.setStorageLocation(request.getStorageLocation());
        commodity.setUpdatedAt(LocalDateTime.now());
        return toResponse(commodityRepository.save(commodity));
    }

    public CommodityResponse updateStatus(Long id, CommodityStatus status) {
        Commodity commodity = require(id);
        if (status != CommodityStatus.ACTIVE
                && shipmentRepository.existsByCommodityIdAndStatusNotIn(id,
                EnumSet.of(ShipmentStatus.DELIVERED, ShipmentStatus.CANCELLED))) {
            throw new BusinessException(
                    "Cannot deactivate commodity while open shipments reference this stock line.");
        }
        if (status != CommodityStatus.ACTIVE && commodity.getQuantity().compareTo(java.math.BigDecimal.ZERO) > 0) {
            throw new BusinessException(
                    "Cannot deactivate commodity with remaining stock. Transfer or ship remaining quantity first.");
        }
        commodity.setStatus(status);
        commodity.setUpdatedAt(LocalDateTime.now());
        return toResponse(commodityRepository.save(commodity));
    }

    public Commodity require(Long id) {
        return commodityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commodity not found with id: " + id));
    }

    public Commodity requireInActiveWarehouse(Long id) {
        Commodity commodity = require(id);
        warehouseService.requireActive(commodity.getWarehouse().getId());
        if (commodity.getStatus() != CommodityStatus.ACTIVE) {
            throw new BusinessException("Commodity is inactive");
        }
        return commodity;
    }

    public void decreaseQuantity(Commodity commodity, BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Quantity to deduct must be greater than zero");
        }
        if (commodity.getQuantity().compareTo(quantity) < 0) {
            throw new BusinessException(
                    "Insufficient stock. Available quantity: " + commodity.getQuantity()
                            + ", requested: " + quantity
                            + ". Shipment or transfer cannot proceed without stock.");
        }
        commodity.setQuantity(commodity.getQuantity().subtract(quantity));
        commodity.setUpdatedAt(LocalDateTime.now());
        commodityRepository.save(commodity);
    }

    public void increaseQuantity(Commodity commodity, BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Quantity to add must be greater than zero");
        }
        commodity.setQuantity(commodity.getQuantity().add(quantity));
        commodity.setUpdatedAt(LocalDateTime.now());
        commodityRepository.save(commodity);
    }

    public static BigDecimal occupyAmount(BigDecimal volume, BigDecimal quantity) {
        if (volume != null && volume.compareTo(BigDecimal.ZERO) > 0) {
            return volume;
        }
        return quantity;
    }

    /**
     * Receive stock into destination warehouse (stock transfer).
     * Matches existing commodity by name+unit in target WH or creates new line.
     */
    public Commodity receiveTransferStock(Warehouse toWarehouse, Commodity source, BigDecimal qty) {
        return commodityRepository.findByWarehouseId(toWarehouse.getId(), org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .filter(c -> c.getName().equalsIgnoreCase(source.getName())
                        && c.getUnit().equalsIgnoreCase(source.getUnit())
                        && c.getStatus() == CommodityStatus.ACTIVE)
                .findFirst()
                .map(existing -> {
                    increaseQuantity(existing, qty);
                    commodityRepository.save(existing);
                    return existing;
                })
                .orElseGet(() -> {
                    Commodity created = Commodity.builder()
                            .commodityCode(SequentialCodes.next("COM", commodityRepository.count() + 1, commodityRepository::existsByCommodityCode))
                            .name(source.getName())
                            .category(source.getCategory())
                            .description(source.getDescription())
                            .quantity(qty)
                            .unit(source.getUnit())
                            .weight(source.getWeight() == null ? BigDecimal.ZERO : source.getWeight())
                            .volume(source.getVolume() == null ? BigDecimal.ZERO : source.getVolume())
                            .warehouse(toWarehouse)
                            .storageLocation(source.getStorageLocation())
                            .status(CommodityStatus.ACTIVE)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return commodityRepository.save(created);
                });
    }

    public void assertCanModifyStock(Commodity commodity) {
        if (commodity.getStatus() != CommodityStatus.ACTIVE) {
            throw new BusinessException("Inactive commodity stock cannot be modified");
        }
    }

    private CommodityResponse toResponse(Commodity commodity) {
        return CommodityResponse.builder()
                .id(commodity.getId())
                .commodityCode(commodity.getCommodityCode())
                .name(commodity.getName())
                .category(commodity.getCategory())
                .description(commodity.getDescription())
                .quantity(commodity.getQuantity())
                .unit(commodity.getUnit())
                .weight(commodity.getWeight())
                .volume(commodity.getVolume())
                .warehouseId(commodity.getWarehouse().getId())
                .storageLocation(commodity.getStorageLocation())
                .status(commodity.getStatus())
                .build();
    }
}
