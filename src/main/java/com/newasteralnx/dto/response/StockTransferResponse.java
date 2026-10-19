package com.newasteralnx.dto.response;

import com.newasteralnx.constant.StockTransferStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class StockTransferResponse {
    private Long id;
    private String transferNumber;
    private Long fromWarehouseId;
    private String fromWarehouseName;
    private Long toWarehouseId;
    private String toWarehouseName;
    private Long commodityId;
    private String commodityName;
    private BigDecimal quantity;
    private StockTransferStatus status;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
