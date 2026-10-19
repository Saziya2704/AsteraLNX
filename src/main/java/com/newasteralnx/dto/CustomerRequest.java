package com.newasteralnx.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 50, message = "Company name must not exceed 50 characters")
    private String companyName;

    @NotBlank(message = "Contact person is required")
    @Size(max = 50, message = "Contact person must not exceed 50 characters")
    private String contactPerson;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^\\+?[0-9\\- ]+$", message = "Enter a valid phone number")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @NotBlank(message = "GST number is required")
    @Size(min = 15, max = 15, message = "GST number must be exactly 15 characters")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Enter a valid GSTIN (e.g. 29ABCDE1234F1Z5)")
    private String gstNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 30, message = "Country must not exceed 30 characters")
    private String country;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;
}
