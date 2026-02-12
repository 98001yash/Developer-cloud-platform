package com.devcloud.build_service.service;

import com.devcloud.build_service.request.CreateBuildRequest;
import com.devcloud.build_service.response.BuildLogResponse;
import com.devcloud.build_service.response.BuildResponse;

import java.util.List;

public interface BuildService {

    // Trigger a new build
    BuildResponse createBuild(CreateBuildRequest request);

    // Get a specific build
    BuildResponse getBuild(Long buildId);

    // Get all builds of a project
    List<BuildResponse> getProjectBuilds(Long projectId);

    // Get logs of a build
    List<BuildLogResponse> getBuildLogs(Long buildId);
}
