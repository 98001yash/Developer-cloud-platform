package com.devcloud.project_service.response;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ProjectResponse {

    private Long id;
    private String name;

    private Long ownerId;

    private List<EnvironmentResponse> environments;
    private Instant createdAt;
}
