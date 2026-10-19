package com.newasteralnx.dto.response;

import com.newasteralnx.constant.DriverStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DriverResponse {
    private Long id;
    private String driverCode;
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private DriverStatus status;
    private Long assignedVehicleId;
}
