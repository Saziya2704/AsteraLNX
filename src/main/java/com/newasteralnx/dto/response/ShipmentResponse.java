package com.newasteralnx.dto.response;

import com.newasteralnx.constant.ShipmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ShipmentResponse {
    private Long id;
    private String shipmentNumber;
    private Long orderId;
    private Long customerId;
    private String origin;
    private String destination;
    private Long warehouseId;
    private Long driverId;
    private Long vehicleId;
    private Long bookingId;
    private Long commodityId;
    private BigDecimal quantity;
    private ShipmentStatus status;
    private LocalDate shipmentDate;
    private LocalDate expectedDelivery;
    private LocalDate actualDelivery;
}
