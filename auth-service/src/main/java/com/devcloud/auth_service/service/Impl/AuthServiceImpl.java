package com.devcloud.auth_service.service.Impl;


import com.devcloud.auth_service.entity.Role;
import com.devcloud.auth_service.entity.User;
import com.devcloud.auth_service.enums.RoleName;
import com.devcloud.auth_service.exceptions.RuntimeConflictException;
import com.devcloud.auth_service.repository.RoleRepository;
import com.devcloud.auth_service.repository.UserRepository;
import com.devcloud.auth_service.request.LoginRequest;
import com.devcloud.auth_service.request.SignupRequest;
import com.devcloud.auth_service.response.AuthResponse;
import com.devcloud.auth_service.security.JwtUtil;
import com.devcloud.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse signup(SignupRequest request) {

        log.info("Signup attempt for email: {}",request.getEmail());


        if(userRepository.existsByEmail(request.getEmail())){
            log.warn("Signup failed: email already exists: {}",request.getEmail());
            throw new RuntimeConflictException("Email already registered");
        }

        Role userRole =roleRepository.findByName(RoleName.ROLE_USER.name())
                .orElseThrow(()-> {
                    log.error("Default role ROLE_USER not found in the database");
                    return new RuntimeException("Default role not found");
                });

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Set.of(userRole))
                .build();
        user = userRepository.save(user);

        log.info("User created successfully with id: {}",user.getId());

        // Generate JWT
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found for email: {}", request.getEmail());
                    return new RuntimeConflictException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: incorrect password for email: {}", request.getEmail());
            throw new RuntimeConflictException("Invalid email or password");
        }
        log.info("User logged in successfully: {}", user.getEmail());

        // Generate JWT
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }
}
