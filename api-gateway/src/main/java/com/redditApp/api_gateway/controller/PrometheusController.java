package com.redditApp.api_gateway.controller;


import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrometheusController {

    private final PrometheusMeterRegistry registry;

    public PrometheusController(PrometheusMeterRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/actuator/prometheus")
    public String scrape() {
        return registry.scrape();
    }
}
