package com.devcloud.build_service.response;


import com.devcloud.build_service.enums.BuildStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuildResponse {

    private Long id;
    private Long projectId;
    private String environment;
    private BuildStatus status;
    private Instant startedAt;
    private Instant finishedAt;
}
