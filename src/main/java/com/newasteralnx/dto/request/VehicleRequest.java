package com.newasteralnx.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class VehicleRequest {

    @NotBlank
    @Size(max = 30)
    private String vehicleNumber;

    @NotBlank
    @Size(max = 50)
    private String vehicleType;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal capacity;

    @Size(max = 50)
    private String registrationNumber;

    private LocalDate insuranceExpiry;
}
