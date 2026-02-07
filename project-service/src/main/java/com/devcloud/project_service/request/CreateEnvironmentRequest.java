package com.devcloud.project_service.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateEnvironmentRequest {

    @NotBlank(message = "Environment name is required")
    private String name;
}
