package com.devcloud.project_service.repository;

import com.devcloud.project_service.entity.Environment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnvironmentRepository extends JpaRepository<Environment, Long> {

    List<Environment> findByProjectId(Long projectId);
}
