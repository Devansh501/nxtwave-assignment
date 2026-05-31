package com.nxtwave.controller;

import com.nxtwave.dto.ApiResponse;
import com.nxtwave.dto.TaskDtos;
import com.nxtwave.dto.UserAnalyticsProjection;
import com.nxtwave.enums.TaskPriority;
import com.nxtwave.enums.TaskStatus;
import com.nxtwave.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<ApiResponse<TaskDtos.TaskResponse>> createTask(@Valid @RequestBody TaskDtos.TaskRequest request) {
        ApiResponse<TaskDtos.TaskResponse> response = ApiResponse.<TaskDtos.TaskResponse>builder()
                .status("success")
                .message("Task created successfully")
                .data(taskService.createTask(request))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or (hasRole('MEMBER') and @securityService.isSameUser(#assigneeId, authentication.name))")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TaskDtos.TaskResponse>>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ApiResponse<Page<TaskDtos.TaskResponse>> response = ApiResponse.<Page<TaskDtos.TaskResponse>>builder()
                .status("success")
                .message("Tasks retrieved successfully")
                .data(taskService.getTasks(status, priority, assigneeId, PageRequest.of(page, size)))
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityService.isTaskAssignee(#taskId, authentication.name)")
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskDtos.TaskResponse>> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status) {

        ApiResponse<TaskDtos.TaskResponse> response = ApiResponse.<TaskDtos.TaskResponse>builder()
                .status("success")
                .message("Task status updated successfully")
                .data(taskService.updateTaskStatus(taskId, status))
                .build();
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("success")
                .message("Task deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<List<com.nxtwave.dto.UserAnalyticsProjection>>> getAnalytics() {

        ApiResponse<List<UserAnalyticsProjection>> response = ApiResponse.<List<com.nxtwave.dto.UserAnalyticsProjection>>builder()
                .status("success")
                .message("User task analytics retrieved successfully")
                .data(taskService.getUserAnalytics())
                .build();

        return ResponseEntity.ok(response);
    }
}