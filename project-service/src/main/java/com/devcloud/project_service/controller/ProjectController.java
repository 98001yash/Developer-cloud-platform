package com.devcloud.project_service.controller;

import com.devcloud.project_service.request.CreateEnvironmentRequest;
import com.devcloud.project_service.request.CreateProjectRequest;
import com.devcloud.project_service.response.EnvironmentResponse;
import com.devcloud.project_service.response.ProjectResponse;
import com.devcloud.project_service.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;


    @PostMapping
    public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest request) {
        log.info("Request to create project '{}'", request.getName());
        return projectService.createProject(request);
    }

    @GetMapping
    public List<ProjectResponse> getProjects() {
        log.info("Request to fetch user projects");
        return projectService.getUserProjects();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable Long id) {
        log.info("Request to fetch project {}", id);
        return projectService.getProjectById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        log.info("Request to delete project {}", id);
        projectService.deleteProject(id);
    }

    @PostMapping("/{id}/environments")
    public EnvironmentResponse addEnvironment(
            @PathVariable Long id,
            @Valid @RequestBody CreateEnvironmentRequest request) {

        log.info("Request to add environment '{}' to project {}",
                request.getName(), id);

        return projectService.addEnvironment(id, request);
    }

    @GetMapping("/{id}/environments")
    public List<EnvironmentResponse> getEnvironments(@PathVariable Long id) {
        log.info("Request to fetch environments for project {}", id);
        return projectService.getEnvironments(id);
    }
}