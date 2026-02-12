package com.devcloud.build_service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBuildRequest {

    private Long projectId;
    private String environment;
    private String repoUrl;
    private String branch;  // default -> main
}
