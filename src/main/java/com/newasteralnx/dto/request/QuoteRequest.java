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
public class QuoteRequest {

    @NotNull
    private Long customerId;

    private Long inquiryId;

    @NotBlank
    @Size(max = 100)
    private String origin;

    @NotBlank
    @Size(max = 100)
    private String destination;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true, message = "Quote amount cannot be negative")
    private BigDecimal amount;

    private LocalDate validUntil;

    @Size(max = 500)
    private String notes;
}
