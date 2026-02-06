package com.devcloud.auth_service.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequest {

    @Email(message = "Invalid email request")
    @NotBlank(message ="Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
