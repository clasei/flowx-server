package com.flowx.services;

import com.flowx.models.Task;
import com.flowx.models.TaskPriority;
import com.flowx.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public int deleteAllCompletedTasks() {
        return taskRepository.deleteByCompleted();
    }

    // create a new task
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

//    public Task createTask(Task task) {
//        if (task.isRepeating() && task.getRepeatInterval() != null) {
//            task.setNextRepeatDate(calculateNextRepeatDate(task.getRepeatInterval()));
//        }
//        return taskRepository.save(task);
//    }


    // get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // get a task by id
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // update a task -- by id
    public Task updateTask(Long id, Task newTaskData) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(newTaskData.getTitle());
                    task.setDescription(newTaskData.getDescription());
                    task.setPriority(TaskPriority.fromLevel(newTaskData.getPriority()).getLevel());
                    task.setCompleted(newTaskData.isCompleted());
                    task.setUpdatedAt(newTaskData.getUpdatedAt());
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    // put = toggle completion status
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    // delete a task -- by id
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Scheduled(fixedRate = 600000) // Every 10 minutes
    public void resetRecurringTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> overdueTasks = taskRepository.findByNextRepeatDateBeforeAndCompleted(now, true);

        for (Task task : overdueTasks) {
            System.out.println("🔄 Resetting recurring task: " + task.getTitle());
            task.setCompleted(false);
            task.setNextRepeatDate(null); // Reset repeat date
            taskRepository.save(task);
        }
    }

}
