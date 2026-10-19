package com.newasteralnx.dto.request;

import com.newasteralnx.constant.PasswordRules;
import com.newasteralnx.constant.Role;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @Size(min = 2, max = 50)
    private String fullName;

    @Size(max = 20)
    @Pattern(regexp = "^\\+?[0-9\\- ]*$", message = "Enter a valid phone number")
    private String phoneNumber;

    private Role role;

    @Pattern(regexp = PasswordRules.REGEXP, message = PasswordRules.MESSAGE)
    private String password;

    private Boolean active;
}
