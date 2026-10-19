package com.newasteralnx.service;

import com.newasteralnx.constant.ReceiptStatus;
import com.newasteralnx.dto.request.ReceiptRequest;
import com.newasteralnx.dto.response.ReceiptResponse;
import com.newasteralnx.entity.Commodity;
import com.newasteralnx.entity.LogisticsOrder;
import com.newasteralnx.entity.Receipt;
import com.newasteralnx.entity.Warehouse;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.ReceiptRepository;
import com.newasteralnx.util.SecurityUtils;
import com.newasteralnx.util.SequentialCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final WarehouseService warehouseService;
    private final OrderService orderService;
    private final CommodityService commodityService;

    public ReceiptResponse create(ReceiptRequest request) {
        if (request.getQuantity() == null || request.getQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Receipt quantity must be greater than zero");
        }
        Warehouse warehouse = warehouseService.requireActive(request.getWarehouseId());
        LogisticsOrder order = orderService.require(request.getOrderId());
        Commodity commodity = commodityService.requireInActiveWarehouse(request.getCommodityId());

        if (!commodity.getWarehouse().getId().equals(warehouse.getId())) {
            throw new BusinessException("Commodity does not belong to the selected warehouse");
        }

        java.math.BigDecimal occupy = CommodityService.occupyAmount(commodity.getVolume(), request.getQuantity());
        warehouseService.consumeCapacity(warehouse, occupy);
        commodityService.increaseQuantity(commodity, request.getQuantity());

        Receipt receipt = Receipt.builder()
                .receiptNumber(SequentialCodes.next("RCP", receiptRepository.count() + 1, receiptRepository::existsByReceiptNumber))
                .warehouse(warehouse)
                .order(order)
                .commodity(commodity)
                .quantity(request.getQuantity())
                .receivedDate(LocalDate.now())
                .receivedBy(SecurityUtils.requireCurrentUserId())
                .status(ReceiptStatus.CONFIRMED)
                .remarks(request.getRemarks())
                .createdAt(LocalDateTime.now())
                .build();
        return toResponse(receiptRepository.save(receipt));
    }

    @Transactional(readOnly = true)
    public Page<ReceiptResponse> list(Pageable pageable) {
        return receiptRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ReceiptResponse get(Long id) {
        return toResponse(receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with id: " + id)));
    }

    private ReceiptResponse toResponse(Receipt receipt) {
        return ReceiptResponse.builder()
                .id(receipt.getId())
                .receiptNumber(receipt.getReceiptNumber())
                .warehouseId(receipt.getWarehouse().getId())
                .orderId(receipt.getOrder().getId())
                .commodityId(receipt.getCommodity().getId())
                .quantity(receipt.getQuantity())
                .receivedDate(receipt.getReceivedDate())
                .receivedBy(receipt.getReceivedBy())
                .status(receipt.getStatus())
                .remarks(receipt.getRemarks())
                .build();
    }
}
