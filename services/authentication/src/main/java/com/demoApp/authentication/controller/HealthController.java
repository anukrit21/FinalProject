package com.demoApp.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for health check endpoints
 */
@RestController
@RequestMapping("/actuator")
public class HealthController {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired(required = false)
    private BuildProperties buildProperties;

    /**
     * Simple health check endpoint
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "authentication-service");
        
        // Add build info if available
        if (buildProperties != null) {
            Map<String, Object> buildInfo = new HashMap<>();
            buildInfo.put("version", buildProperties.getVersion());
            buildInfo.put("build.time", buildProperties.getTime());
            health.put("build", buildInfo);
        }
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Detailed health check including database connectivity
     * @return detailed health status
     */
    @GetMapping("/health/details")
    public ResponseEntity<Map<String, Object>> healthDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("status", "UP");
        details.put("service", "authentication-service");
        
        // Check database connectivity
        try {
            Query query = entityManager.createNativeQuery("SELECT 1");
            query.getSingleResult();
            Map<String, String> db = new HashMap<>();
            db.put("status", "UP");
            details.put("database", db);
        } catch (Exception e) {
            Map<String, String> db = new HashMap<>();
            db.put("status", "DOWN");
            db.put("error", e.getMessage());
            details.put("database", db);
            details.put("status", "DOWN");
        }
        
        // Add build info if available
        if (buildProperties != null) {
            Map<String, Object> buildInfo = new HashMap<>();
            buildInfo.put("version", buildProperties.getVersion());
            buildInfo.put("build.time", buildProperties.getTime());
            details.put("build", buildInfo);
        }
        
        return ResponseEntity.ok(details);
    }
    
    /**
     * Information about the application
     * @return application info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "authentication-service");
        info.put("description", "Authentication and authorization service");
        
        // Add build info if available
        if (buildProperties != null) {
            Map<String, Object> buildInfo = new HashMap<>();
            buildInfo.put("name", buildProperties.getName());
            buildInfo.put("version", buildProperties.getVersion());
            buildInfo.put("build.time", buildProperties.getTime());
            info.put("build", buildInfo);
        }
        
        return ResponseEntity.ok(info);
    }
} 