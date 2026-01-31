package com.medshare.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * MedShare Hub - Secure Healthcare Data Exchange Platform
 * 
 * Main application entry point for the Spring Boot application.
 * Implements Attribute-Based Access Control (ABAC) for fine-grained
 * authorization in healthcare data sharing.
 * 
 * @author MedShare Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class MedShareHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedShareHubApplication.class, args);
    }
}
