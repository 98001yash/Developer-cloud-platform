package com.devcloud.deployment_service.service;

import com.devcloud.deployment_service.dtos.DeployRequest;
import com.devcloud.deployment_service.dtos.DeploymentDetailsResponse;
import com.devcloud.deployment_service.dtos.DeploymentResponse;

import java.util.List;

public interface DeploymentService {

    DeploymentResponse deploy(Long userId, Long projectId, DeployRequest request);

    List<DeploymentResponse> getProjectDeployments(Long userId, Long projectId);

    DeploymentDetailsResponse getDeploymentDetails(Long userId, Long deploymentId);
}
