package com.devcloud.build_service.service.Impl;

import com.devcloud.build_service.auth.UserContextHolder;
import com.devcloud.build_service.entities.Build;
import com.devcloud.build_service.entities.BuildLog;
import com.devcloud.build_service.enums.BuildStatus;
import com.devcloud.build_service.exceptions.ResourceNotFoundException;
import com.devcloud.build_service.repository.BuildLogRepository;
import com.devcloud.build_service.repository.BuildRepository;
import com.devcloud.build_service.request.CreateBuildRequest;
import com.devcloud.build_service.response.BuildLogResponse;
import com.devcloud.build_service.response.BuildResponse;
import com.devcloud.build_service.service.BuildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuildServiceImpl implements BuildService {

    private final BuildRepository buildRepository;
    private final BuildLogRepository buildLogRepository;

    @Override
    public BuildResponse createBuild(CreateBuildRequest request) {

        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} requested build for project {}",userId, request.getProjectId());

        Build build = Build.builder()
                .projectId(request.getProjectId())
                .environment(request.getEnvironment())
                .status(BuildStatus.PENDING)
                .startedAt(Instant.now())
                .build();

        build = buildRepository.save(build);
        log.info("Build {} created with status PENDING",build.getId());

        // simulated execution
         simulateBuildExecution(build);

         return mapToBuildResponse(build);
    }

    @Override
    public BuildResponse getBuild(Long buildId) {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} fetching build {}",userId, buildId);

        Build build = buildRepository.findById(buildId)
                .orElseThrow(()-> {
                    log.warn("Build {} not found",buildId);
                    return new ResourceNotFoundException("Build not found");
                });

        return mapToBuildResponse(build);
    }

    @Override
    public List<BuildResponse> getProjectBuilds(Long projectId) {
        return List.of();
    }

    @Override
    public List<BuildLogResponse> getBuildLogs(Long buildId) {
        return List.of();
    }




    // Simulated Build Execution (temporary)
    private void simulateBuildExecution(Build build) {

        try {
            log.info("Starting simulated execution for build {}", build.getId());

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

            log.info("Build {} finished successfully", build.getId());

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


    // Mapping Methods
    private BuildResponse mapToBuildResponse(Build build) {
        return BuildResponse.builder()
                .id(build.getId())
                .projectId(build.getProjectId())
                .environment(build.getEnvironment())
                .status(build.getStatus())
                .startedAt(build.getStartedAt())
                .finishedAt(build.getFinishedAt())
                .build();
    }

    private BuildLogResponse mapToLogResponse(BuildLog log) {
        return BuildLogResponse.builder()
                .timestamp(log.getTimestamp())
                .message(log.getMessage())
                .build();
    }
}
