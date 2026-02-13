package com.devcloud.deployment_service.service.Impl;

import com.devcloud.deployment_service.dtos.DeployRequest;
import com.devcloud.deployment_service.dtos.DeploymentDetailsResponse;
import com.devcloud.deployment_service.dtos.DeploymentResponse;
import com.devcloud.deployment_service.entity.Deployment;
import com.devcloud.deployment_service.enums.DeploymentStatus;
import com.devcloud.deployment_service.repository.DeploymentRepository;
import com.devcloud.deployment_service.service.DeploymentExecutor;
import com.devcloud.deployment_service.service.DeploymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentServiceImpl implements DeploymentService {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentExecutor deploymentExecutor;


    @Override
    public DeploymentResponse deploy(Long userId, Long projectId, DeployRequest request) {

        log.info("User {} deploying build {} to {} environment for project {}",
                userId, request.getBuildId(), request.getEnvironment(), projectId);

        Deployment deployment = Deployment.builder()
                .projectId(projectId)
                .buildId(request.getBuildId())
                .environment(request.getEnvironment())
                .status(DeploymentStatus.PENDING)
                .imageName("project-" + projectId + "-build-" + request.getBuildId())
                .containerId(null)
                .port(null)
                .build();

        deployment = deploymentRepository.save(deployment);

        log.info("Deployment {} created with status {}",
                deployment.getId(), deployment.getStatus());

        deploymentExecutor.executeDeployment(deployment.getId());
        return mapToResponse(deployment);
    }

    @Override
    public List<DeploymentResponse> getProjectDeployments(Long userId, Long projectId) {

        log.info("Fetching deployments for project {} by user {}", projectId, userId);

        return deploymentRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeploymentDetailsResponse getDeploymentDetails(Long userId, Long deploymentId) {

        log.info("Fetching deployment {} for user {}", deploymentId, userId);

        Deployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> {
                    log.warn("Deployment {} not found", deploymentId);
                    return new RuntimeException("Deployment not found");
                });

        return mapToDetailsResponse(deployment);
    }

    @Override
    public DeploymentResponse stopDeployment(Long userId, Long deploymentId) {

        log.info("User {} requested STOP for deployment {}",userId, deploymentId);

        Deployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(()-> new RuntimeException("Deployment not found"));

        if(deployment.getContainerId() == null){
            throw new RuntimeException("no running container for this deployment");
        }
        try {
            String command = "docker stop " + deployment.getContainerId();
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Docker stop failed");
            }

            deployment.setStatus(DeploymentStatus.STOPPED);
            deploymentRepository.save(deployment);
            log.info("Deployment {} stopped successfully", deploymentId);

            return mapToResponse(deployment);
        } catch (Exception ex) {
            log.error("Failed to stop deployment {}: {}", deploymentId, ex.getMessage());
            throw new RuntimeException("Stop deployment failed");
        }
    }

    @Override
    public DeploymentResponse restartDeployment(Long userId, Long deploymentId) {

        log.info("User {} requested for development {}", userId, deploymentId);

            Deployment deployment = deploymentRepository.findById(deploymentId)
                    .orElseThrow(() -> new RuntimeException("Deployment not found"));

            if (deployment.getContainerId() == null) {
                throw new RuntimeException("No container for this deployment");
            }

            try {
                String command = "docker restart " + deployment.getContainerId();
                Process process = Runtime.getRuntime().exec(command);
                int exitCode = process.waitFor();

                if (exitCode != 0) {
                    throw new RuntimeException("Docker restart failed");
                }
                deployment.setStatus(DeploymentStatus.RUNNING);
                deploymentRepository.save(deployment);

                log.info("Deployment {} restarted successfully", deploymentId);
                return mapToResponse(deployment);

            } catch (Exception ex) {
                log.error("Failed to restart deployment {}: {}", deploymentId, ex.getMessage());
                throw new RuntimeException("Restart deployment failed");
            }
        }

    //  MAPPERS

    private DeploymentResponse mapToResponse(Deployment deployment) {
        return DeploymentResponse.builder()
                .id(deployment.getId())
                .projectId(deployment.getProjectId())
                .buildId(deployment.getBuildId())
                .environment(deployment.getEnvironment())
                .status(deployment.getStatus())
                .serviceUrl(buildServiceUrl(deployment))
                .createdAt(deployment.getCreatedAt())
                .build();
    }

    private DeploymentDetailsResponse mapToDetailsResponse(Deployment deployment) {
        return DeploymentDetailsResponse.builder()
                .id(deployment.getId())
                .projectId(deployment.getProjectId())
                .buildId(deployment.getBuildId())
                .environment(deployment.getEnvironment())
                .status(deployment.getStatus())
                .serviceUrl(buildServiceUrl(deployment))
                .createdAt(deployment.getCreatedAt())
                .updatedAt(deployment.getUpdatedAt())
                .build();
    }

    private String buildServiceUrl(Deployment deployment) {
        if (deployment.getPort() == null) {
            return null;
        }
        return "http://localhost:" + deployment.getPort();
    }
}
