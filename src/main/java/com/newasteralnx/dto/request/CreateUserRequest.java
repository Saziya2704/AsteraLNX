package com.newasteralnx.dto.request;

import com.newasteralnx.constant.PasswordRules;
import com.newasteralnx.constant.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = PasswordRules.REGEXP, message = PasswordRules.MESSAGE)
    private String password;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^\\+?[0-9\\- ]*$", message = "Enter a valid phone number")
    private String phoneNumber;

    @NotNull(message = "Role is required")
    private Role role;
}
