package com.newasteralnx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String message;
    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
}