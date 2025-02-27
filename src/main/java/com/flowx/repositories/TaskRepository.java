package com.flowx.repositories;

import com.flowx.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


// test to add delete all
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByNextRepeatDateBeforeAndCompleted(LocalDateTime now, boolean completed);


    @Transactional
    @Modifying
    @Query("DELETE FROM Task t WHERE t.completed = true")
    int deleteByCompleted();
}