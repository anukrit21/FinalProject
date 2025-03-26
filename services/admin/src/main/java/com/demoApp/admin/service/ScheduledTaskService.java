package com.demoApp.admin.service;

import com.demoApp.admin.dto.ScheduledTaskDTO;
import com.demoApp.admin.entity.ScheduledTask;
import com.demoApp.admin.exception.ResourceNotFoundException;
import com.demoApp.admin.repository.ScheduledTaskRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduledTaskService {
    
    private final ScheduledTaskRepository scheduledTaskRepository;
    private final ModelMapper modelMapper;
    
    public List<ScheduledTask> getAllScheduledTasks() {
        return scheduledTaskRepository.findAll();
    }
    
    public ScheduledTask getScheduledTaskById(Long id) {
        return scheduledTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduled task not found with id: " + id));
    }
    
    public List<ScheduledTask> getScheduledTasksByService(String serviceName) {
        return scheduledTaskRepository.findByServiceName(serviceName);
    }

    // ✅ Corrected: Method to get tasks by status
    public List<ScheduledTask> getScheduledTasksByStatus(ScheduledTask.Status status) {
        return scheduledTaskRepository.findByStatus(status);
    }
    
    // ✅ Corrected: Method to get paginated tasks by service name
    public Page<ScheduledTask> getScheduledTasksByServicePaged(String serviceName, Pageable pageable) {
        return scheduledTaskRepository.findByServiceNameOrderByNextExecutionTimeAsc(serviceName, pageable);
    }
    
    // ✅ Corrected: Method to get paginated tasks by status
    public Page<ScheduledTask> getScheduledTasksByStatusPaged(ScheduledTask.Status status, Pageable pageable) {
        return scheduledTaskRepository.findByStatusOrderByNextExecutionTimeAsc(status, pageable);
    }
    
    @Transactional
    public ScheduledTask createScheduledTask(ScheduledTaskDTO scheduledTaskDTO) {
        ScheduledTask scheduledTask = modelMapper.map(scheduledTaskDTO, ScheduledTask.class);
        return scheduledTaskRepository.save(scheduledTask);
    }
    
    @Transactional
    public ScheduledTask updateScheduledTask(Long id, ScheduledTaskDTO scheduledTaskDTO) {
        ScheduledTask existingTask = getScheduledTaskById(id);
        
        if (scheduledTaskDTO.getTaskName() != null) {
            existingTask.setTaskName(scheduledTaskDTO.getTaskName());
        }
        if (scheduledTaskDTO.getDescription() != null) {
            existingTask.setDescription(scheduledTaskDTO.getDescription());
        }
        if (scheduledTaskDTO.getServiceName() != null) {
            existingTask.setServiceName(scheduledTaskDTO.getServiceName());
        }
        if (scheduledTaskDTO.getCronExpression() != null) {
            existingTask.setCronExpression(scheduledTaskDTO.getCronExpression());
        }
        if (scheduledTaskDTO.getStatus() != null) {
            existingTask.setStatus(scheduledTaskDTO.getStatus());
        }
        if (scheduledTaskDTO.getScheduledTime() != null) {
            existingTask.setScheduledTime(scheduledTaskDTO.getScheduledTime());
        }
        if (scheduledTaskDTO.getLastExecutionTime() != null) {
            existingTask.setLastExecutionTime(scheduledTaskDTO.getLastExecutionTime());
        }
        if (scheduledTaskDTO.getNextExecutionTime() != null) {
            existingTask.setNextExecutionTime(scheduledTaskDTO.getNextExecutionTime());
        }
        if (scheduledTaskDTO.getLastExecutionResult() != null) {
            existingTask.setLastExecutionResult(scheduledTaskDTO.getLastExecutionResult());
        }
        
        return scheduledTaskRepository.save(existingTask);
    }
    
    @Transactional
    public void deleteScheduledTask(Long id) {
        ScheduledTask scheduledTask = getScheduledTaskById(id);
        scheduledTaskRepository.delete(scheduledTask);
    }
    
    // ✅ Corrected: Method to update task execution status
    @Transactional
    public ScheduledTask updateTaskExecution(Long id, ScheduledTask.Status newStatus, String executionResult) {
        ScheduledTask task = getScheduledTaskById(id);
        task.setStatus(newStatus);
        task.setLastExecutionResult(executionResult);
        task.setLastExecutionTime(LocalDateTime.now());
        
        // If task is completed or failed, reschedule it
        if (newStatus == ScheduledTask.Status.COMPLETED || newStatus == ScheduledTask.Status.FAILED) {
            task.setStatus(ScheduledTask.Status.SCHEDULED);
            task.setNextExecutionTime(LocalDateTime.now().plusHours(24)); // Default 24-hour reschedule
        }
        
        return scheduledTaskRepository.save(task);
    }
    
    // ✅ Corrected: Scheduled task execution checker
    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    @Transactional
    public void checkScheduledTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledTask> dueTasks = scheduledTaskRepository.findByNextExecutionTimeBefore(now);
        
        for (ScheduledTask task : dueTasks) {
            if (task.getStatus() == ScheduledTask.Status.SCHEDULED) {
                task.setStatus(ScheduledTask.Status.RUNNING);
                scheduledTaskRepository.save(task);
                
                try {
                    Thread.sleep(2000); // Simulate task execution
                    updateTaskExecution(task.getId(), ScheduledTask.Status.COMPLETED, "Task executed successfully");
                } catch (Exception e) {
                    updateTaskExecution(task.getId(), ScheduledTask.Status.FAILED, "Execution failed: " + e.getMessage());
                }
            }
        }
    }
    
    public Optional<ScheduledTask> findByTaskNameAndServiceName(String taskName, String serviceName) {
        return scheduledTaskRepository.findByTaskNameAndServiceName(taskName, serviceName);
    }
    
    public ScheduledTaskDTO convertToDTO(ScheduledTask scheduledTask) {
        return ScheduledTaskDTO.fromEntity(scheduledTask);
    }
    
    public List<ScheduledTaskDTO> convertToDTOs(List<ScheduledTask> scheduledTasks) {
        return scheduledTasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
