package com.newasteralnx.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReceiptRequest {

    @NotNull
    private Long warehouseId;

    @NotNull
    private Long orderId;

    @NotNull
    private Long commodityId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @Size(max = 500)
    private String remarks;
}
