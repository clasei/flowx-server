package com.flowx.services;

import com.flowx.models.Task;
import com.flowx.models.TaskPriority;
import com.flowx.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // create a new task
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

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

    // delete a task -- by id
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // put = toggle completion status
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

}
