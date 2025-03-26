package com.demoApp.menu_module.client;

import com.demoApp.menu_module.client.dto.UserDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "owner-service", path = "/api/v1/users")
public interface UserServiceClient {

    @GetMapping("/{username}")
    UserDetailsResponse getUserByUsername(@PathVariable String username);
} 