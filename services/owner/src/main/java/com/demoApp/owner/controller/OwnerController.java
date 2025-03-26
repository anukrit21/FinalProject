package com.demoApp.owner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.demoApp.owner.dto.OwnerDTO;
import com.demoApp.owner.service.OwnerService;

@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping("/{id}")
    @PreAuthorize("@ownerAuthorizationService.isOwnerAuthorized(#id)")
    public ResponseEntity<OwnerDTO> getOwnerById(@PathVariable Long id) {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ownerAuthorizationService.isOwnerAuthorized(#id)")
    public ResponseEntity<OwnerDTO> updateOwner(@PathVariable Long id, @Valid @RequestBody OwnerDTO ownerDTO) {
        return ResponseEntity.ok(ownerService.updateOwner(id, ownerDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ownerAuthorizationService.isOwnerAuthorized(#id)")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }
}