package com.newasteralnx.dto.request;

import com.newasteralnx.constant.PasswordRules;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = PasswordRules.REGEXP, message = PasswordRules.MESSAGE)
    private String newPassword;
}
