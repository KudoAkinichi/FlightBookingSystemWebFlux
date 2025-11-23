package com.flightapp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoAuditing
@ComponentScan(basePackages = {"com"})
@EnableReactiveMongoRepositories(basePackages = "com.repository")
@OpenAPIDefinition(
        info = @Info(
                title = "Flight Booking System API",
                version = "1.0.0",
                description = "Reactive Flight Booking System with MongoDB and WebFlux"
        )
)
public class FlightBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightBookingApplication.class, args);
    }
}
