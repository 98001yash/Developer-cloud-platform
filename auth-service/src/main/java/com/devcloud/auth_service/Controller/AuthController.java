package com.devcloud.auth_service.Controller;

import com.devcloud.auth_service.entity.User;
import com.devcloud.auth_service.repository.UserRepository;
import com.devcloud.auth_service.request.LoginRequest;
import com.devcloud.auth_service.request.SignupRequest;
import com.devcloud.auth_service.response.AuthResponse;
import com.devcloud.auth_service.response.UserResponse;
import com.devcloud.auth_service.security.JwtUtil;
import com.devcloud.auth_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        log.info("Received signup request for email: {}", request.getEmail());
        return authService.signup(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for email: {}", request.getEmail());
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = header.substring(7);
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Fetched profile for user: {}", email);

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(
                        user.getRoles()
                                .stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toSet())
                )
                .build();
    }

}
