package com.devcloud.build_service.service.Impl;

import com.devcloud.build_service.entities.Build;
import com.devcloud.build_service.entities.BuildLog;
import com.devcloud.build_service.enums.BuildStatus;
import com.devcloud.build_service.repository.BuildLogRepository;
import com.devcloud.build_service.repository.BuildRepository;
import com.devcloud.build_service.service.BuildExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;


@Component
@RequiredArgsConstructor
@Slf4j
public class BuildExecutorImpl implements BuildExecutor {

    private final BuildRepository buildRepository;
    private final BuildLogRepository buildLogRepository;



    @Override
    @Async("buildExecutor")
    public void executeBuild(Build build) {

        try {
            log.info("Async build {} started", build.getId());

            build.setStatus(BuildStatus.RUNNING);
            buildRepository.save(build);

            // Create working directory
            String workDir = "builds/build-" + build.getId();
            new File(workDir).mkdirs();

            // Step 1: Clone repo
            runCommand(build, workDir,
                    "git", "clone", "-b",
                    build.getBranch() == null ? "main" : build.getBranch(),
                    build.getRepoUrl(),
                    ".");

            // Step 2: Docker build
            String imageTag = "project-" + build.getProjectId() + "-build-" + build.getId();

            runCommand(build, workDir,
                    "docker", "build", "-t", imageTag, ".");

            // Step 3: Run container
            runCommand(build, workDir,
                    "docker", "run", "--rm", imageTag);

            build.setStatus(BuildStatus.SUCCESS);
            build.setFinishedAt(Instant.now());
            buildRepository.save(build);

            saveLog(build, "Build completed successfully");
            log.info("Build {} completed successfully", build.getId());

        } catch (Exception ex) {
            log.error("Build {} failed: {}", build.getId(), ex.getMessage());

            build.setStatus(BuildStatus.FAILED);
            build.setFinishedAt(Instant.now());
            buildRepository.save(build);

            saveLog(build, "Build failed: " + ex.getMessage());
        }
    }


    private void saveLog(Build build, String message) {
        BuildLog logEntry = BuildLog.builder()
                .build(build)
                .timestamp(Instant.now())
                .message(message)
                .build();

        buildLogRepository.save(logEntry);
    }


    // Helper methods

    private void runCommand(Build build, String workDir, String... command)
            throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(workDir));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                saveLog(build, line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Command failed: " + String.join(" ", command));
        }
    }

}
