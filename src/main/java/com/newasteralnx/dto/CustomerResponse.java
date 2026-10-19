package com.newasteralnx.dto;

import com.newasteralnx.entity.CustomerStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CustomerResponse {

    private Long id;
    private String customerCode;
    private String companyName;
    private String contactPerson;
    private String phone;
    private String email;
    private String gstNumber;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private CustomerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
