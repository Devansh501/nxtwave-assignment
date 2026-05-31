package com.nxtwave.dto;

import com.nxtwave.enums.TaskPriority;
import com.nxtwave.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TaskDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskRequest{
        @NotBlank(message = "Title is required")
        private String title;
        private String description;

        @NotNull(message = "Priority is required")
        private TaskPriority  priority;

        @FutureOrPresent(message = "due_date must be a future date")
        private LocalDateTime dueDate;

        private Long assigneeId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskResponse implements Serializable{
        private Long id;
        private String title;
        private String description;
        private TaskPriority priority;
        private TaskStatus status;
        private String assigneeUsername;
        private LocalDateTime dueDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
