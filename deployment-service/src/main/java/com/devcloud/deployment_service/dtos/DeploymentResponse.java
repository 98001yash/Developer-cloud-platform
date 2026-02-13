package com.devcloud.deployment_service.dtos;


import com.devcloud.deployment_service.enums.DeploymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DeploymentResponse {

    private Long id;
    private Long projectId;
    private Long buildId;
    private String environment;

    private DeploymentStatus status;
    private String serviceUrl;
    private Instant createdAt;
}
