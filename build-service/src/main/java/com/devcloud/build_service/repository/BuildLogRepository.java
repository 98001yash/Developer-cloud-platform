package com.devcloud.build_service.repository;

import com.devcloud.build_service.entities.BuildLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildLogRepository extends JpaRepository<BuildLog, Long> {

    List<BuildLog> findByBuildIdOrderByTimestampAsc(Long buildId);
}
