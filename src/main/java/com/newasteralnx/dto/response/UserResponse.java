package com.newasteralnx.dto.response;

import com.newasteralnx.constant.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
