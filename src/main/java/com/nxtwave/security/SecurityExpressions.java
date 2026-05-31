package com.nxtwave.security;

import com.nxtwave.entity.Task;
import com.nxtwave.entity.User;
import com.nxtwave.repository.TaskRepository;
import com.nxtwave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component("securityService")
@Transactional(readOnly = true)
public class SecurityExpressions {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public boolean isTaskAssignee(Long taskId, String username) {
        Task task = taskRepository.findById(taskId).orElse(null);
        return task != null && task.getAssignee() != null && task.getAssignee().getUsername().equals(username);
    }

    public boolean isSameUser(Long requestedUserId, String username) {
        if (requestedUserId == null) return false;
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && user.getId().equals(requestedUserId);
    }
}
