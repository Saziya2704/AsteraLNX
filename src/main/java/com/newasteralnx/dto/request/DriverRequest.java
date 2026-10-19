package com.newasteralnx.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DriverRequest {

    private Long userId;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @Email
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(max = 40)
    private String licenseNumber;

    @NotNull
    private LocalDate licenseExpiry;

    private Long assignedVehicleId;
}
