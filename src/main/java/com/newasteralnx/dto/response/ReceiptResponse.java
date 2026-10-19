package com.newasteralnx.dto.response;

import com.newasteralnx.constant.ReceiptStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ReceiptResponse {
    private Long id;
    private String receiptNumber;
    private Long warehouseId;
    private Long orderId;
    private Long commodityId;
    private BigDecimal quantity;
    private LocalDate receivedDate;
    private Long receivedBy;
    private ReceiptStatus status;
    private String remarks;
}
