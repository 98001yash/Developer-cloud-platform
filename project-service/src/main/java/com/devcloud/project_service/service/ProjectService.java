package com.devcloud.project_service.service;

import com.devcloud.project_service.request.CreateEnvironmentRequest;
import com.devcloud.project_service.request.CreateProjectRequest;
import com.devcloud.project_service.response.EnvironmentResponse;
import com.devcloud.project_service.response.ProjectResponse;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request);

    List<ProjectResponse> getUserProjects();

    ProjectResponse getProjectById(Long projectId);

    void deleteProject(Long projectId);

    EnvironmentResponse addEnvironment(Long projectId, CreateEnvironmentRequest request);

    List<EnvironmentResponse> getEnvironments(Long projectId);
}