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
import com.devcloud.build_service.service.BuildExecutor;
import com.devcloud.build_service.service.BuildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuildServiceImpl implements BuildService {

    private final BuildRepository buildRepository;
    private final BuildLogRepository buildLogRepository;
    private final BuildExecutor buildExecutor;


    @Override
    public BuildResponse createBuild(CreateBuildRequest request) {

        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} requested build for project {}",userId, request.getProjectId());

        Build build = Build.builder()
                .projectId(request.getProjectId())
                .environment(request.getEnvironment())
                .repoUrl(request.getRepoUrl())
                .branch(request.getBranch())
                .status(BuildStatus.PENDING)
                .startedAt(Instant.now())
                .build();

        build = buildRepository.save(build);
        log.info("Build {} created with status PENDING",build.getId());

        // simulated execution
        buildExecutor.executeBuild(build);
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

        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} fetching builds for project {}",userId, projectId);

        return buildRepository.findByProjectIdOrderByIdDesc(projectId)
                .stream()
                .map(this::mapToBuildResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildLogResponse> getBuildLogs(Long buildId) {

        Long userId = UserContextHolder.getCurrentUserId();

        log.info("User {} fetching build logs for build {}", userId, buildId);
        ;

        if (!buildRepository.existsById(buildId)) {
            log.warn("Build {} not found while fetching logs", buildId);
            throw new ResourceNotFoundException("Build not found");
        }

        return buildLogRepository.findByBuildIdOrderByTimestampAsc(buildId)
                .stream()
                .map(this::mapToLogResponse)
                .collect(Collectors.toList());
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
