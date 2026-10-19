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
public class InquiryRequest {

    private Long customerId;

    @NotBlank
    @Size(max = 100)
    private String subject;

    @NotBlank
    @Size(max = 100)
    private String origin;

    @NotBlank
    @Size(max = 100)
    private String destination;

    @Size(max = 255)
    private String cargoDescription;

    @NotNull
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @Size(max = 500)
    private String remarks;
}
