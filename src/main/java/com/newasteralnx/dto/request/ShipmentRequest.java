package com.newasteralnx.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ShipmentRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private Long warehouseId;

    @NotNull
    private Long driverId;

    @NotNull
    private Long vehicleId;

    private Long bookingId;

    @NotNull
    private Long commodityId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal quantity;

    private LocalDate shipmentDate;

    @NotNull
    private LocalDate expectedDelivery;
}
