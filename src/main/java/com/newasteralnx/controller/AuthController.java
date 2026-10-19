package com.newasteralnx.controller;

import com.newasteralnx.dto.LoginRequest;
import com.newasteralnx.dto.LoginResponse;
import com.newasteralnx.dto.request.ChangePasswordRequest;
import com.newasteralnx.dto.request.ForgotPasswordRequest;
import com.newasteralnx.dto.request.ResetPasswordRequest;
import com.newasteralnx.dto.response.MessageResponse;
import com.newasteralnx.dto.response.UserResponse;
import com.newasteralnx.service.AuthService;
import com.newasteralnx.service.UserService;
import com.newasteralnx.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.getUser(SecurityUtils.requireCurrentUserId()));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        return ResponseEntity.ok(authService.logout());
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}
