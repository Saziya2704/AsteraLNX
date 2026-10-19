package com.newasteralnx.dto;

import com.newasteralnx.entity.CustomerStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerStatusRequest {

    @NotNull(message = "Status is required")
    private CustomerStatus status;
}