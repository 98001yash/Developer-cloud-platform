package com.devcloud.deployment_service.service.Impl;


import com.devcloud.deployment_service.entity.Deployment;
import com.devcloud.deployment_service.enums.DeploymentStatus;
import com.devcloud.deployment_service.repository.DeploymentRepository;
import com.devcloud.deployment_service.service.DeploymentExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentExecutorImpl implements DeploymentExecutor {

    private final DeploymentRepository deploymentRepository;


    @Async("deploymentExecutor")
    @Override
    public void executeDeployment(Long deploymentId) {

        log.info("Async deployment {} started", deploymentId);

        Deployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> new RuntimeException("Deployment not found"));

        try {
            // Update status to STARTING
            deployment.setStatus(DeploymentStatus.STARTING);
            deploymentRepository.save(deployment);

            // Assign random port
            int port = new Random().nextInt(10000) + 10000;
            deployment.setPort(port);

            String imageName = deployment.getImageName();

            // Run container
            String command = String.format(
                    "docker run -d -p %d:9090 %s",
                    port,
                    imageName
            );

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String containerId = reader.readLine();

            int exitCode = process.waitFor();

            if (exitCode != 0 || containerId == null) {
                throw new RuntimeException("Docker run failed");
            }

            deployment.setContainerId(containerId.trim());
            deployment.setStatus(DeploymentStatus.RUNNING);
            deploymentRepository.save(deployment);

            log.info("Deployment {} running on port {}", deploymentId, port);

        } catch (Exception ex) {
            log.error("Deployment {} failed: {}", deploymentId, ex.getMessage());

            deployment.setStatus(DeploymentStatus.FAILED);
            deploymentRepository.save(deployment);
        }
    }
}
