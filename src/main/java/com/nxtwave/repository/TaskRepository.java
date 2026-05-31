package com.nxtwave.repository;

import com.nxtwave.dto.UserAnalyticsProjection;
import com.nxtwave.entity.Task;
import com.nxtwave.enums.TaskPriority;
import com.nxtwave.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:assigneeId IS NULL OR t.assignee.id = :assigneeId)")
    Page<Task> findTasksWithFilters(
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("assigneeId") Long assigneeId,
            Pageable pageable);

    @Query(value = """
        SELECT 
            u.username AS username,
            COUNT(CASE WHEN t.due_date < CURRENT_TIMESTAMP AND t.status != 'DONE' THEN 1 END) AS overdueCount,
            AVG(CASE WHEN t.status = 'DONE' THEN EXTRACT(EPOCH FROM (t.updated_at - t.created_at)) / 3600.0 END) AS avgCompletionTimeHours
        FROM users u
        INNER JOIN tasks t ON u.id = t.assignee_id
        GROUP BY u.id, u.username
        """, nativeQuery = true)
    List<UserAnalyticsProjection> getUserAnalytics();

}
