package com.newasteralnx.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CommodityRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 50)
    private String category;

    @Size(max = 255)
    private String description;

    @NotNull
    @DecimalMin(value = "0.00", message = "Quantity cannot be negative")
    private BigDecimal quantity;

    @NotBlank
    @Size(max = 20)
    private String unit;

    @DecimalMin(value = "0.00", message = "Weight cannot be negative")
    private BigDecimal weight;

    @DecimalMin(value = "0.00", message = "Volume cannot be negative")
    private BigDecimal volume;

    @NotNull
    private Long warehouseId;

    @Size(max = 50)
    private String storageLocation;
}
