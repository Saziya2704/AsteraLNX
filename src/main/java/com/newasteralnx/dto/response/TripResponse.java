package com.newasteralnx.dto.response;

import com.newasteralnx.constant.TripStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TripResponse {
    private Long id;
    private String tripNumber;
    private Long shipmentId;
    private Long driverId;
    private Long vehicleId;
    private String origin;
    private String destination;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMinutes;
    private BigDecimal distance;
    private TripStatus status;
}
