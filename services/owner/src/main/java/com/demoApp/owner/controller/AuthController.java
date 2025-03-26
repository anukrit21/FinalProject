package com.demoApp.owner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.demoApp.owner.dto.AuthResponseDTO;
import com.demoApp.owner.dto.LoginDTO;
import com.demoApp.owner.dto.OwnerDTO;
import com.demoApp.owner.entity.Owner;
import com.demoApp.owner.payload.ApiResponse;
import com.demoApp.owner.security.JwtTokenProvider;
import com.demoApp.owner.service.OwnerService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final OwnerService ownerService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerOwner(@Valid @RequestBody OwnerDTO ownerDTO) {
        Owner createdOwner = ownerService.createOwner(ownerDTO, "ROLE_OWNER");
        String message = "Owner registered successfully with ID: " + createdOwner.getId();
        return ResponseEntity.ok(new ApiResponse(true, message));
    }
}
