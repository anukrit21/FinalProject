package com.demoApp.menu_module.security;

import com.demoApp.menu_module.client.UserServiceClient;
import com.demoApp.menu_module.client.dto.UserDetailsResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    @Override
    @CircuitBreaker(name = "userService", fallbackMethod = "getDefaultUserDetails")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Fetching user details for username: {}", username);
        
        UserDetailsResponse userDetails = userServiceClient.getUserByUsername(username);
        
        if (userDetails == null) {
            log.error("User not found: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        
        return new User(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
    
} 