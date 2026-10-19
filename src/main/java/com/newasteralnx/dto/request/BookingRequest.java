package com.newasteralnx.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long orderId;

    private Long warehouseId;

    @NotBlank
    @Size(max = 100)
    private String pickup;

    @NotBlank
    @Size(max = 100)
    private String destination;

    private Long vehicleId;

    private Long driverId;

    @NotNull
    private LocalDate scheduledDate;
}
