package com.newasteralnx.dto.response;

import com.newasteralnx.constant.InvoiceStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long customerId;
    private Long orderId;
    private Long shipmentId;
    private LocalDate invoiceDate;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
    private InvoiceStatus status;
    private LocalDate dueDate;
    private LocalDate paidDate;
}
