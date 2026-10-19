package com.newasteralnx.dto.request;

import com.newasteralnx.constant.OrderPriority;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderRequest {

    @NotNull
    private Long customerId;

    private Long quoteId;

    @NotBlank
    @Size(max = 100)
    private String origin;

    @NotBlank
    @Size(max = 100)
    private String destination;

    @NotBlank
    @Size(max = 100)
    private String commodityName;

    @NotNull
    @DecimalMin(value = "0.01", message = "Order quantity must be greater than 0")
    private BigDecimal quantity;

    @DecimalMin(value = "0.00", message = "Weight cannot be negative")
    private BigDecimal weight;

    @DecimalMin(value = "0.00", message = "Volume cannot be negative")
    private BigDecimal volume;

    private OrderPriority priority;
}
