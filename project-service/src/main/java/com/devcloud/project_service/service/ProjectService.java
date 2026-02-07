package com.devcloud.project_service.service;

import com.devcloud.project_service.request.CreateEnvironmentRequest;
import com.devcloud.project_service.request.CreateProjectRequest;
import com.devcloud.project_service.response.EnvironmentResponse;
import com.devcloud.project_service.response.ProjectResponse;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(Long ownerId, CreateProjectRequest request);

    List<ProjectResponse> getUserProjects(Long ownerId);

    ProjectResponse getProjectById(Long ownerId, Long projectId);

    void deleteProject(Long ownerId, Long projectId);

    EnvironmentResponse addEnvironment(Long ownerId, Long projectId, CreateEnvironmentRequest request);

    List<EnvironmentResponse> getEnvironments(Long ownerId, Long projectId);

}
