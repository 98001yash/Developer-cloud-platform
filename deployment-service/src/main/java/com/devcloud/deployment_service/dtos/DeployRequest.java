package com.devcloud.deployment_service.dtos;


import lombok.Data;

@Data
public class DeployRequest {

    private Long buildId;
    private String environment;
}
