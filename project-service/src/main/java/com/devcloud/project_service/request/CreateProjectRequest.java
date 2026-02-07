package com.devcloud.project_service.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;
}
