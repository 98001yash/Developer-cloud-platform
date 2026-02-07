package com.devcloud.project_service.response;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EnvironmentResponse {

    private Long id;
    private String name;
    private Instant createdAt;
}
