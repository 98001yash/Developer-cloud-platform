package com.devcloud.deployment_service.controller;


import com.devcloud.deployment_service.auth.UserContextHolder;
import com.devcloud.deployment_service.dtos.DeployRequest;
import com.devcloud.deployment_service.dtos.DeploymentDetailsResponse;
import com.devcloud.deployment_service.dtos.DeploymentResponse;
import com.devcloud.deployment_service.service.DeploymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(".deployments")
@RequiredArgsConstructor
@Slf4j
public class DeploymentController {

    private final DeploymentService deploymentService;


    @PostMapping("/projects/{projectId}")
    public DeploymentResponse deploy(
            @PathVariable Long projectId,
            @RequestBody DeployRequest request
    ) {
        Long userId = UserContextHolder.getCurrentUserId();

        log.info("User {} requested deployment for project {}", userId, projectId);

        return deploymentService.deploy(userId, projectId, request);
    }


    @GetMapping("/projects/{projectId}")
    public List<DeploymentResponse> getProjectDeployments(
            @PathVariable Long projectId
    ) {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} fetching deployments for project {}", userId, projectId);
        return deploymentService.getProjectDeployments(userId, projectId);
    }


    @GetMapping("/{deploymentId}")
    public DeploymentDetailsResponse getDeploymentDetails(
            @PathVariable Long deploymentId
    ) {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} fetching deployment details {}", userId, deploymentId);
        return deploymentService.getDeploymentDetails(userId, deploymentId);
    }


    @PostMapping("/{deploymentId}/stop")
    public DeploymentResponse stopDeployment(
            @PathVariable Long deploymentId
    ) {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} requested stop for deployment {}", userId, deploymentId);
        return deploymentService.stopDeployment(userId, deploymentId);
    }


    @PostMapping("/{deploymentId}/restart")
    public DeploymentResponse restartDeployment(
            @PathVariable Long deploymentId
    ) {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} requested restart for deployment {}", userId, deploymentId);
        return deploymentService.restartDeployment(userId, deploymentId);
    }
}
