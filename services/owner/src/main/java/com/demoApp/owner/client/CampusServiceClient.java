package com.demoApp.owner.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.demoApp.owner.client.fallback.CampusServiceFallback;
import com.demoApp.owner.config.FeignClientConfig;

@FeignClient(
    name = "campus-service",
    fallback = CampusServiceFallback.class,
    configuration = FeignClientConfig.class
)
public interface CampusServiceClient {
    @GetMapping("/api/v1/campuses/mess/{messId}")
    ResponseEntity<Object> getCampusesByMessId(@PathVariable Long messId);

    @GetMapping("/api/v1/campuses/{campusId}")
    ResponseEntity<Object> getCampusDetails(@PathVariable Long campusId);
}