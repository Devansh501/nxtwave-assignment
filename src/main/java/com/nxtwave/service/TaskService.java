package com.nxtwave.service;

import com.nxtwave.dto.TaskDtos;
import com.nxtwave.dto.UserAnalyticsProjection;
import com.nxtwave.entity.Task;
import com.nxtwave.entity.User;
import com.nxtwave.enums.TaskPriority;
import com.nxtwave.enums.TaskStatus;
import com.nxtwave.exceptions.ResourceNotFoundException;
import com.nxtwave.repository.TaskRepository;
import com.nxtwave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public TaskDtos.TaskResponse createTask(TaskDtos.TaskRequest request) {
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TaskStatus.TODO)
                .assignee(assignee)
                .dueDate(request.getDueDate())
                .build();

        return mapToResponse(taskRepository.save(task));
    }

    @Cacheable(value = "tasks", key = "{#status, #priority, #assigneeId, #pageable.pageNumber}")
    @Transactional(readOnly = true)
    public Page<TaskDtos.TaskResponse> getTasks(TaskStatus status, TaskPriority priority, Long assigneeId, Pageable pageable) {
        return taskRepository.findTasksWithFilters(status, priority, assigneeId, pageable).map(this::mapToResponse);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Transactional
    public TaskDtos.TaskResponse updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!isValidTransition(task.getStatus(), newStatus)) {
            throw new IllegalArgumentException("Invalid state transition from " + task.getStatus() + " to " + newStatus);
        }

        task.setStatus(newStatus);
        return mapToResponse(taskRepository.save(task));
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found");
        }
        taskRepository.deleteById(taskId);
    }

    private boolean isValidTransition(TaskStatus current, TaskStatus next) {
        if (current == next) return true;
        if (next == TaskStatus.BLOCKED) return true;

        return switch (current) {
            case TODO -> next == TaskStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == TaskStatus.IN_REVIEW;
            case IN_REVIEW -> next == TaskStatus.DONE;
            case BLOCKED -> next == TaskStatus.TODO || next == TaskStatus.IN_PROGRESS;
            case DONE -> false;
        };
    }

    private TaskDtos.TaskResponse mapToResponse(Task task) {
        return TaskDtos.TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .assigneeUsername(task.getAssignee() != null ? task.getAssignee().getUsername() : null)
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserAnalyticsProjection> getUserAnalytics() {
        return taskRepository.getUserAnalytics();
    }
}