package com.devcloud.build_service.controller;


import com.devcloud.build_service.auth.UserContextHolder;
import com.devcloud.build_service.request.CreateBuildRequest;
import com.devcloud.build_service.response.BuildLogResponse;
import com.devcloud.build_service.response.BuildResponse;
import com.devcloud.build_service.service.BuildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/builds")
@RequiredArgsConstructor
@Slf4j
public class BuildController {


    private final BuildService buildService;


    @PostMapping
    public BuildResponse createBuild(@RequestBody CreateBuildRequest request){

        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} triggered build for project {}",userId, request.getProjectId());

        return buildService.createBuild(request);
    }

    // Get build by id
    @GetMapping("/{buildId}")
    public BuildResponse getBuild(@PathVariable Long buildId) {

        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} fetching build {}", userId, buildId);

        return buildService.getBuild(buildId);
    }

    // Get all builds for a project
    @GetMapping("/project/{projectId}")
    public List<BuildResponse> getProjectBuilds(@PathVariable Long projectId) {

        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} fetching builds for project {}", userId, projectId);

        return buildService.getProjectBuilds(projectId);
    }

    // Get build logs
    @GetMapping("/{buildId}/logs")
    public List<BuildLogResponse> getBuildLogs(@PathVariable Long buildId) {

        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} fetching logs for build {}", userId, buildId);

        return buildService.getBuildLogs(buildId);
    }
}
