package com.devcloud.project_service.repository;

import com.devcloud.project_service.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  ProjectRepository extends JpaRepository<Project,Long> {

    // get all projects owned by the user
    List<Project> findByOwnerId(Long ownerId);


    // get specific project for a user (ownership check)
    Optional<Project> findByIdAndOwnerId(Long id, Long ownerId);


    // check it project exists for user
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}
