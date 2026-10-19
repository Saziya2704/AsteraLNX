package com.newasteralnx.service.impl;

import com.newasteralnx.dto.LoginRequest;
import com.newasteralnx.dto.LoginResponse;
import com.newasteralnx.dto.request.ChangePasswordRequest;
import com.newasteralnx.dto.request.ForgotPasswordRequest;
import com.newasteralnx.dto.request.ResetPasswordRequest;
import com.newasteralnx.dto.response.MessageResponse;
import com.newasteralnx.entity.PasswordResetToken;
import com.newasteralnx.entity.User;
import com.newasteralnx.exception.AuthenticationException;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.repository.PasswordResetTokenRepository;
import com.newasteralnx.repository.UserRepository;
import com.newasteralnx.security.JwtService;
import com.newasteralnx.service.AuthService;
import com.newasteralnx.service.MailService;
import com.newasteralnx.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MailService mailService;

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    log.info("Failed login for email={}", email);
                    return new AuthenticationException("Invalid email or password");
                });

        if (Boolean.TRUE.equals(user.getDeleted()) || !Boolean.TRUE.equals(user.getActive())) {
            log.info("Failed login for inactive account email={}", email);
            throw new AuthenticationException("Your account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.info("Failed login for email={}", email);
            throw new AuthenticationException("Invalid email or password");
        }

        boolean rememberMe = Boolean.TRUE.equals(request.getRememberMe());
        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                rememberMe
        );

        log.info("User login userId={} email={}", user.getId(), user.getEmail());

        return LoginResponse.builder()
                .message("Login successful")
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public MessageResponse logout() {
        return new MessageResponse("Logout successful. Discard the JWT on the client.");
    }

    @Override
    public MessageResponse changePassword(ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BusinessException("Confirm password must match the new password");
        }
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new BusinessException("New password must be different from the current password");
        }

        User user = userRepository.findById(SecurityUtils.requireCurrentUserId())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password changed userId={}", user.getId());
        return new MessageResponse("Password changed successfully");
    }

    @Override
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            if (Boolean.TRUE.equals(user.getDeleted()) || !Boolean.TRUE.equals(user.getActive())) {
                return;
            }
            String otp = String.format("%06d", new java.util.Random().nextInt(1_000_000));
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(otp)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusMinutes(30))
                    .used(false)
                    .build();
            passwordResetTokenRepository.save(resetToken);
            boolean sent = mailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), otp);
            if (sent) {
                log.info("Password reset email dispatched for email={}", email);
            } else {
                log.info("Password reset prepared for email={} (email logged — configure SMTP to deliver)", email);
            }
        });
        // Always generic message (do not reveal whether the account exists)
        return new MessageResponse(
                "If an account exists for that email, a password reset message has been sent with a one-time code and link. The code is valid for 30 minutes.");
    }

    @Override
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Invalid or expired reset token"));

        if (Boolean.TRUE.equals(resetToken.getUsed()) || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Invalid or expired reset token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        resetToken.setUsed(true);
        userRepository.save(user);

        log.info("Password reset completed userId={}", user.getId());
        return new MessageResponse("Password reset successful");
    }
}
