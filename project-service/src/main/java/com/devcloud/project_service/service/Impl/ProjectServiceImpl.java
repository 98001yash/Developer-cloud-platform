package com.devcloud.project_service.service.Impl;

import com.devcloud.project_service.auth.UserContextHolder;
import com.devcloud.project_service.entity.Environment;
import com.devcloud.project_service.entity.Project;
import com.devcloud.project_service.exceptions.ResourceNotFoundException;
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

    // Helper to get authenticated user
    private Long getCurrentUserId() {
        Long userId = UserContextHolder.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Unauthenticated request: userId missing");
        }
        return userId;
    }

    @Override
    public ProjectResponse createProject(CreateProjectRequest request) {

        Long ownerId = getCurrentUserId();
        log.info("Creating project '{}' for user {}", request.getName(), ownerId);

        Project project = Project.builder()
                .name(request.getName())
                .ownerId(ownerId)
                .build();

        project = projectRepository.save(project);

        log.info("Project created with id {}", project.getId());
        return mapToProjectResponse(project);
    }

    @Override
    public List<ProjectResponse> getUserProjects() {

        Long ownerId = getCurrentUserId();
        log.info("Fetching projects for user {}", ownerId);

        return projectRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponse getProjectById(Long projectId) {

        Long ownerId = getCurrentUserId();
        log.info("Fetching project {} for user {}", projectId, ownerId);

        Project project = projectRepository.findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(() -> {
                    log.warn("Project {} not found for user {}", projectId, ownerId);
                    return new ResourceNotFoundException("Project not found");
                });

        return mapToProjectResponse(project);
    }

    @Override
    public void deleteProject(Long projectId) {

        Long ownerId = getCurrentUserId();
        log.info("Deleting project {} for user {}", projectId, ownerId);

        Project project = projectRepository.findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(() -> {
                    log.warn("Delete failed: project {} not found for user {}", projectId, ownerId);
                    return new ResourceNotFoundException("Project not found");
                });

        projectRepository.delete(project);
        log.info("Project {} deleted", projectId);
    }

    @Override
    public EnvironmentResponse addEnvironment(Long projectId, CreateEnvironmentRequest request) {

        Long ownerId = getCurrentUserId();
        log.info("Adding environment '{}' to project {} for user {}",
                request.getName(), projectId, ownerId);

        Project project = projectRepository.findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(() -> {
                    log.warn("Environment creation failed: project {} not found", projectId);
                    return new ResourceNotFoundException("Project not found");
                });

        Environment environment = Environment.builder()
                .name(request.getName())
                .project(project)
                .build();

        environment = environmentRepository.save(environment);

        log.info("Environment '{}' created with id {}",
                environment.getName(), environment.getId());

        return mapToEnvironmentResponse(environment);
    }

    @Override
    public List<EnvironmentResponse> getEnvironments(Long projectId) {

        Long ownerId = getCurrentUserId();
        log.info("Fetching environments for project {} (user {})",
                projectId, ownerId);

        // Verify ownership
        projectRepository.findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(() -> {
                    log.warn("Environment fetch failed: project {} not found", projectId);
                    return new ResourceNotFoundException("Project not found");
                });

        return environmentRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToEnvironmentResponse)
                .collect(Collectors.toList());
    }

    // ---------------- Mapping Helpers ----------------

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
