package com.newasteralnx.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ForgotPasswordResponse {
    private String message;
    /** 6-digit OTP returned for local/demo only (no real email gateway). */
    private String demoOtp;
}
