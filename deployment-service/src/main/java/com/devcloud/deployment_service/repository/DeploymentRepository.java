package com.devcloud.deployment_service.repository;

import com.devcloud.deployment_service.entity.Deployment;
import com.devcloud.deployment_service.enums.DeploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  DeploymentRepository extends JpaRepository<Deployment, Long> {


    // all deployment of a project
    List<Deployment> findByProjectId(Long projectId);


    // latest running deployment of a project
    Optional<Deployment> findFirstByProjectIdAndStatusOrderByCreatedAtDesc(
            Long projectId,
            DeploymentStatus status
    );


    // specific deployment of a project
    Optional<Deployment> findByIdAndProjectId(Long deploymentId, Long projectId);
}
