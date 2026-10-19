package com.newasteralnx.dto.response;

import com.newasteralnx.constant.TrackingStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class GpsTrackingResponse {
    private Long id;
    private Long shipmentId;
    private Long tripId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime timestamp;
    private BigDecimal speed;
    private TrackingStatus status;
}
