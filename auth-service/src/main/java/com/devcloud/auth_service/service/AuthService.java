package com.devcloud.auth_service.service;

import com.devcloud.auth_service.request.LoginRequest;
import com.devcloud.auth_service.request.SignupRequest;
import com.devcloud.auth_service.response.AuthResponse;

public interface AuthService {

    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
}
