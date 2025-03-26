package com.demoApp.mess.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.demoApp.mess.dto.ApiResponse;
import com.demoApp.mess.dto.MessDTO;
import com.demoApp.mess.entity.Mess;
import com.demoApp.mess.service.MessService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messes")
public class MessController {

    private final MessService messService;

    public MessController(MessService messService) {
        this.messService = messService;
    }

    @GetMapping
    public ResponseEntity<List<Mess>> getAllMesses() {
        return ResponseEntity.ok(messService.getAllMesses());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Mess>> getApprovedMesses() {
        return ResponseEntity.ok(messService.getApprovedMesses());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Mess>> getPendingMesses() {
        return ResponseEntity.ok(messService.getPendingMesses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mess> getMessById(@PathVariable Long id) {
        return ResponseEntity.ok(messService.getMessById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MESS') or hasRole('ADMIN')")
    public ResponseEntity<Mess> updateMess(@PathVariable Long id, @RequestBody MessDTO messDTO) {
        return ResponseEntity.ok(messService.updateMess(id, messDTO));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Mess> approveMess(@PathVariable Long id) {
        return ResponseEntity.ok(messService.approveMess(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteMess(@PathVariable Long id) {
        messService.deleteMess(id);
        return ResponseEntity.ok(new ApiResponse(true, "Mess deleted successfully"));
    }
}
