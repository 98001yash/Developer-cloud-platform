package com.devcloud.build_service.repository;

import com.devcloud.build_service.entities.Build;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  BuildRepository extends JpaRepository<Build, Long> {

    List<Build> findByProjectIdOrderByIdDesc(Long projectId);
}
