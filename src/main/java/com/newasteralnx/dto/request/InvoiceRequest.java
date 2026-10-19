package com.newasteralnx.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoiceRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long orderId;

    private Long shipmentId;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal subtotal;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal tax;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal discount;

    private LocalDate dueDate;
}
