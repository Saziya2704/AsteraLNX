package com.newasteralnx.dto.response;

import com.newasteralnx.constant.OrderPriority;
import com.newasteralnx.constant.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private Long quoteId;
    private String origin;
    private String destination;
    private LocalDate orderDate;
    private String commodityName;
    private BigDecimal quantity;
    private BigDecimal weight;
    private BigDecimal volume;
    private OrderStatus status;
    private OrderPriority priority;
    private LocalDateTime createdAt;
}
