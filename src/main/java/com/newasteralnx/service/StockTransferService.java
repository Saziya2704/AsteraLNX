package com.newasteralnx.service;

import com.newasteralnx.constant.StockTransferStatus;
import com.newasteralnx.dto.request.StockTransferRequest;
import com.newasteralnx.dto.response.StockTransferResponse;
import com.newasteralnx.entity.Commodity;
import com.newasteralnx.entity.StockTransfer;
import com.newasteralnx.entity.Warehouse;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.StockTransferRepository;
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
public class StockTransferService {

    private final StockTransferRepository stockTransferRepository;
    private final WarehouseService warehouseService;
    private final CommodityService commodityService;

    /**
     * Transfer stock between warehouses.
     * Deducts from source commodity immediately; creates/updates destination commodity stock.
     */
    public StockTransferResponse createAndComplete(StockTransferRequest request) {
        if (request.getFromWarehouseId().equals(request.getToWarehouseId())) {
            throw new BusinessException("Source and destination warehouse must be different");
        }

        Warehouse from = warehouseService.requireActive(request.getFromWarehouseId());
        Warehouse to = warehouseService.requireActive(request.getToWarehouseId());
        Commodity source = commodityService.requireInActiveWarehouse(request.getCommodityId());

        if (!source.getWarehouse().getId().equals(from.getId())) {
            throw new BusinessException("Commodity does not belong to the source warehouse");
        }
        if (source.getQuantity().compareTo(request.getQuantity()) < 0) {
            throw new BusinessException(
                    "Insufficient stock for transfer. Available: " + source.getQuantity()
                            + ", requested: " + request.getQuantity());
        }

        // Deduct source
        commodityService.decreaseQuantity(source, request.getQuantity());
        BigDecimal occupy = CommodityService.occupyAmount(source.getVolume(), request.getQuantity());
        // proportional occupy restore on source WH
        warehouseService.restoreCapacity(from, occupy);
        warehouseService.consumeCapacity(to, occupy);

        // Add to destination (same product name/unit under destination WH)
        commodityService.receiveTransferStock(to, source, request.getQuantity());

        StockTransfer transfer = StockTransfer.builder()
                .transferNumber(SequentialCodes.next("STF", stockTransferRepository.count() + 1,
                        stockTransferRepository::existsByTransferNumber))
                .fromWarehouse(from)
                .toWarehouse(to)
                .commodity(source)
                .quantity(request.getQuantity())
                .status(StockTransferStatus.COMPLETED)
                .remarks(request.getRemarks())
                .createdBy(SecurityUtils.requireCurrentUserId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();

        return toResponse(stockTransferRepository.save(transfer));
    }

    @Transactional(readOnly = true)
    public Page<StockTransferResponse> list(Pageable pageable) {
        return stockTransferRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public StockTransferResponse get(Long id) {
        return toResponse(stockTransferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock transfer not found: " + id)));
    }

    private StockTransferResponse toResponse(StockTransfer t) {
        return StockTransferResponse.builder()
                .id(t.getId())
                .transferNumber(t.getTransferNumber())
                .fromWarehouseId(t.getFromWarehouse().getId())
                .fromWarehouseName(t.getFromWarehouse().getName())
                .toWarehouseId(t.getToWarehouse().getId())
                .toWarehouseName(t.getToWarehouse().getName())
                .commodityId(t.getCommodity().getId())
                .commodityName(t.getCommodity().getName())
                .quantity(t.getQuantity())
                .status(t.getStatus())
                .remarks(t.getRemarks())
                .createdAt(t.getCreatedAt())
                .completedAt(t.getCompletedAt())
                .build();
    }
}
