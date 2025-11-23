package com.config;

import org.springframework.context.annotation.Configuration;

/**
 * Router configuration for functional endpoints
 * Currently using annotated controllers, but this can be extended
 * for functional reactive endpoints if needed
 */
@Configuration
public class RouterConfig {
    // Can add RouterFunction beans here for functional endpoints
    // Example:
    // @Bean
    // public RouterFunction<ServerResponse> route(FlightHandler handler) {
    //     return RouterFunctions
    //         .route(GET("/api/v1/flights").and(accept(APPLICATION_JSON)), handler::list)
    //         .andRoute(POST("/api/v1/flights").and(accept(APPLICATION_JSON)), handler::create);
    // }
}