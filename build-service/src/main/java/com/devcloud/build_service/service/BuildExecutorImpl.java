package com.devcloud.build_service.service;

import com.devcloud.build_service.entities.Build;
import com.devcloud.build_service.entities.BuildLog;
import com.devcloud.build_service.enums.BuildStatus;
import com.devcloud.build_service.repository.BuildLogRepository;
import com.devcloud.build_service.repository.BuildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;


@Component
@RequiredArgsConstructor
@Slf4j
public class BuildExecutorImpl implements BuildExecutor{

    private final BuildRepository buildRepository;
    private final BuildLogRepository buildLogRepository;



    @Override
    @Async("buildExecutor")
    public void executeBuild(Build build) {
        try {
            log.info("Async build {} started", build.getId());

            build.setStatus(BuildStatus.RUNNING);
            buildRepository.save(build);

            saveLog(build, "Cloning repository...");
            Thread.sleep(1000);

            saveLog(build, "Installing dependencies...");
            Thread.sleep(1000);

            saveLog(build, "Running build...");
            Thread.sleep(1000);

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
}
