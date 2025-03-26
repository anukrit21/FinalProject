package com.demoApp.admin.controller;

import com.demoApp.admin.dto.ApiResponse;
import com.demoApp.admin.dto.ScheduledTaskDTO;
import com.demoApp.admin.entity.ScheduledTask;
import com.demoApp.admin.service.ScheduledTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scheduled-tasks")
@RequiredArgsConstructor
public class ScheduledTaskController {

    private final ScheduledTaskService scheduledTaskService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScheduledTaskDTO>> getAllScheduledTasks() {
        List<ScheduledTask> tasks = scheduledTaskService.getAllScheduledTasks();
        return ResponseEntity.ok(scheduledTaskService.convertToDTOs(tasks));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduledTaskDTO> getScheduledTaskById(@PathVariable Long id) {
        ScheduledTask task = scheduledTaskService.getScheduledTaskById(id);
        return ResponseEntity.ok(scheduledTaskService.convertToDTO(task));
    }

    @GetMapping("/service/{serviceName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScheduledTaskDTO>> getScheduledTasksByService(@PathVariable String serviceName) {
        List<ScheduledTask> tasks = scheduledTaskService.getScheduledTasksByService(serviceName);
        return ResponseEntity.ok(scheduledTaskService.convertToDTOs(tasks));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScheduledTaskDTO>> getScheduledTasksByStatus(
            @PathVariable ScheduledTask.Status status) {  // ✅ Consistent reference
        List<ScheduledTask> tasks = scheduledTaskService.getScheduledTasksByStatus(status);
        return ResponseEntity.ok(scheduledTaskService.convertToDTOs(tasks));
    }

    @GetMapping("/service/{serviceName}/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ScheduledTaskDTO>> getScheduledTasksByServicePaged(
            @PathVariable String serviceName, Pageable pageable) {
        Page<ScheduledTask> tasks = scheduledTaskService.getScheduledTasksByServicePaged(serviceName, pageable);
        return ResponseEntity.ok(tasks.map(scheduledTaskService::convertToDTO));
    }

    @GetMapping("/status/{status}/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ScheduledTaskDTO>> getScheduledTasksByStatusPaged(
            @PathVariable ScheduledTask.Status status, Pageable pageable) {  // ✅ Consistent reference
        Page<ScheduledTask> tasks = scheduledTaskService.getScheduledTasksByStatusPaged(status, pageable);
        return ResponseEntity.ok(tasks.map(scheduledTaskService::convertToDTO));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduledTaskDTO> createScheduledTask(@Valid @RequestBody ScheduledTaskDTO scheduledTaskDTO) {
        ScheduledTask task = scheduledTaskService.createScheduledTask(scheduledTaskDTO);
        return new ResponseEntity<>(scheduledTaskService.convertToDTO(task), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduledTaskDTO> updateScheduledTask(
            @PathVariable Long id,
            @Valid @RequestBody ScheduledTaskDTO scheduledTaskDTO) {
        ScheduledTask task = scheduledTaskService.updateScheduledTask(id, scheduledTaskDTO);
        return ResponseEntity.ok(scheduledTaskService.convertToDTO(task));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteScheduledTask(@PathVariable Long id) {
        scheduledTaskService.deleteScheduledTask(id);
        return ResponseEntity.ok(new ApiResponse(true, "Scheduled task deleted successfully"));
    }

    @PatchMapping("/{id}/execution")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduledTaskDTO> updateTaskExecution(
            @PathVariable Long id,
            @RequestParam ScheduledTask.Status newStatus,  // ✅ Consistent reference
            @RequestParam(required = false) String executionResult) {

        ScheduledTask task = scheduledTaskService.updateTaskExecution(id, newStatus, executionResult);
        return ResponseEntity.ok(scheduledTaskService.convertToDTO(task));
    }

    @PostMapping("/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> checkScheduledTasks() {
        scheduledTaskService.checkScheduledTasks();
        return ResponseEntity.ok(new ApiResponse(true, "Scheduled tasks check triggered"));
    }
}
