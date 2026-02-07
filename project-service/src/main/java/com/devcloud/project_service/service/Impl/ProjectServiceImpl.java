package com.devcloud.project_service.service.Impl;


import com.devcloud.project_service.entity.Environment;
import com.devcloud.project_service.entity.Project;
import com.devcloud.project_service.repository.EnvironmentRepository;
import com.devcloud.project_service.repository.ProjectRepository;
import com.devcloud.project_service.request.CreateEnvironmentRequest;
import com.devcloud.project_service.request.CreateProjectRequest;
import com.devcloud.project_service.response.EnvironmentResponse;
import com.devcloud.project_service.response.ProjectResponse;
import com.devcloud.project_service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;


    @Override
    public ProjectResponse createProject(Long ownerId, CreateProjectRequest request) {

        log.info("Creating a project {} for user {}",request.getName(), ownerId);

        Project project = Project.builder()
                .name(request.getName())
                .ownerId(ownerId)
                .build();

        project = projectRepository.save(project);

        log.info("Project created with id {}",project.getId());
        return mapToProjectResponse(project);
    }

    @Override
    public List<ProjectResponse> getUserProjects(Long ownerId) {
        return List.of();
    }

    @Override
    public ProjectResponse getProjectById(Long ownerId, Long projectId) {
        return null;
    }

    @Override
    public void deleteProject(Long ownerId, Long projectId) {

    }

    @Override
    public EnvironmentResponse addEnvironment(Long ownerId, Long projectId, CreateEnvironmentRequest request) {
        return null;
    }

    @Override
    public List<EnvironmentResponse> getEnvironments(Long ownerId, Long projectId) {
        return List.of();
    }


    // Helper methods
    private ProjectResponse mapToProjectResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .ownerId(project.getOwnerId())
                .createdAt(project.getCreatedAt())
                .environments(
                        project.getEnvironments()
                                .stream()
                                .map(this::mapToEnvironmentResponse)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private EnvironmentResponse mapToEnvironmentResponse(Environment environment) {
        return EnvironmentResponse.builder()
                .id(environment.getId())
                .name(environment.getName())
                .createdAt(environment.getCreatedAt())
                .build();
    }
}
