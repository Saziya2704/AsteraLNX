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
public class WarehouseRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotBlank
    @Size(max = 50)
    private String city;

    @NotBlank
    @Size(max = 50)
    private String state;

    @NotBlank
    @Size(max = 30)
    private String country;

    @NotBlank
    @Size(max = 20)
    private String postalCode;

    @Size(max = 50)
    private String managerName;

    @NotNull
    @DecimalMin(value = "0.01", message = "Capacity must be greater than zero")
    private BigDecimal capacity;
}
