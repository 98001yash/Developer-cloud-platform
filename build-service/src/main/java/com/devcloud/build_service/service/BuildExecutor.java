package com.devcloud.build_service.service;

import com.devcloud.build_service.entities.Build;

public interface BuildExecutor {

    void executeBuild(Build build);
}
