package com.newasteralnx.service;

import com.newasteralnx.dto.LoginRequest;
import com.newasteralnx.dto.LoginResponse;
import com.newasteralnx.dto.request.ChangePasswordRequest;
import com.newasteralnx.dto.request.ForgotPasswordRequest;
import com.newasteralnx.dto.request.ResetPasswordRequest;
import com.newasteralnx.dto.response.MessageResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    MessageResponse logout();

    MessageResponse changePassword(ChangePasswordRequest request);

    MessageResponse forgotPassword(ForgotPasswordRequest request);

    MessageResponse resetPassword(ResetPasswordRequest request);
}
