package com.controller;

import com.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class HealthController {

    /**
     * Health check endpoint
     * GET /api/v1/health
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<ApiResponse<Map<String, Object>>>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Flight Booking System WebFlux");
        health.put("timestamp", LocalDateTime.now());
        health.put("version", "1.0.0");

        return Mono.just(ResponseEntity.ok(
                ApiResponse.success("Service is running", health)
        ));
    }
}