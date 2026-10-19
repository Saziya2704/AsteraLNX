package com.newasteralnx.dto.response;

import com.newasteralnx.constant.VehicleStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class VehicleResponse {
    private Long id;
    private String vehicleNumber;
    private String vehicleType;
    private BigDecimal capacity;
    private VehicleStatus status;
    private String registrationNumber;
    private LocalDate insuranceExpiry;
}
