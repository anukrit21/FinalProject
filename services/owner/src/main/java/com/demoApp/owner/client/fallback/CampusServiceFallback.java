package com.demoApp.owner.client.fallback;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.demoApp.owner.client.CampusServiceClient;

@Component
public class CampusServiceFallback implements CampusServiceClient {

    private static final Logger log = LoggerFactory.getLogger(CampusServiceFallback.class);
    
    @Override
    public ResponseEntity<Object> getCampusesByMessId(Long messId) {
        log.warn("Falling back to empty response for campus service, mess ID: {}", messId);
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Override
    public ResponseEntity<Object> getCampusDetails(Long campusId) {
        log.error("Fallback: Unable to connect to campus service for campus ID: {}", campusId);
        return ResponseEntity.ok().build();
    }
}