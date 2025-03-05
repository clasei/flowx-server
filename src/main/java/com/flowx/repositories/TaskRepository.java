package com.flowx.repositories;

import com.flowx.models.Task;
import com.flowx.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByNextRepeatDateBeforeAndCompleted(LocalDateTime now, boolean completed);
    List<Task> findByCreatedBy(User user);

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM Task t WHERE t.completed = true")
//    int deleteByCompleted();

    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.createdBy.id = :userId AND t.completed = true")
    int deleteCompletedTasksByUser(@Param("userId") Long userId);


}