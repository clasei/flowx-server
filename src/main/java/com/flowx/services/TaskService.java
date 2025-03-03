package com.flowx.services;

import com.flowx.models.Task;
import com.flowx.models.TaskPriority;
import com.flowx.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

//    public Task createTask(Task task) {
//        return taskRepository.save(task);
//    }

    public Task createTask(Task task) {
        System.out.println("🔥 Incoming Task: " + task);  // Debugging

        // Ensure isRepeating is correctly assigned
        boolean isActuallyRepeating = task.isRepeating(); // Capture the original value

        if (isActuallyRepeating && task.getRepeatInterval() != null) {
            task.setNextRepeatDate(calculateNextRepeatDate(task.getRepeatInterval()));
        }

        task.setRepeating(isActuallyRepeating); // 🔥 Ensure it is not lost

        Task savedTask = taskRepository.save(task);
        System.out.println("✅ Saved Task: " + savedTask);  // Debugging

        return savedTask;
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
                    task.setPriority(newTaskData.getPriority());
                    task.setCompleted(newTaskData.isCompleted());
                    task.setUpdatedAt(LocalDateTime.now());

                    // Preserve repeating status
                    task.setRepeating(newTaskData.isRepeating());
                    task.setRepeatInterval(newTaskData.getRepeatInterval());

                    // 🔥 Always recalculate nextRepeatDate if task is repeating
                    if (task.isRepeating() && task.getRepeatInterval() != null) {
                        task.setNextRepeatDate(calculateNextRepeatDate(task.getRepeatInterval()));
                    } else {
                        // ❌ If repeating is turned off, clear nextRepeatDate
                        task.setNextRepeatDate(null);
                    }

                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }




    // put = save toggled task
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    // toggle task completion
//    public Task toggleTaskCompletion(Long id) {
//        return taskRepository.findById(id)
//                .map(task -> {
//                    task.setCompleted(!task.isCompleted());
//                    task.setUpdatedAt(LocalDateTime.now());
//
//                    if (task.isRepeating() && task.isCompleted() && task.getRepeatInterval() != null) {
//                        task.setNextRepeatDate(calculateNextRepeatDate(task.getRepeatInterval()));
//                    } else {
//                        task.setNextRepeatDate(null);
//                    }
//
//                    return taskRepository.save(task);
//                })
//                .orElseThrow(() -> new RuntimeException("Task not found"));
//    }

    // // WEIRD WAY -- reset next repeat date when undo --------------
//    public Task toggleTaskCompletion(Long id) {
//        return taskRepository.findById(id)
//                .map(task -> {
//                    task.setCompleted(!task.isCompleted());
//                    task.setUpdatedAt(LocalDateTime.now());
//
//                    if (task.isCompleted() && task.isRepeating() && task.getRepeatInterval() != null) {
//                        // 🔥 Ensure next repeat date is set correctly
//                        task.setNextRepeatDate(calculateNextRepeatDate(task.getRepeatInterval()));
//                    } else if (!task.isRepeating()) {
//                        // 🔥 If it's not repeating, reset nextRepeatDate
//                        task.setNextRepeatDate(null);
//                    }
//
//                    return taskRepository.save(task);
//                })
//                .orElseThrow(() -> new RuntimeException("Task not found"));
//    }

    public Task toggleTaskCompletion(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setCompleted(!task.isCompleted());
                    task.setUpdatedAt(LocalDateTime.now());

                    // ❌ DO NOT reset nextRepeatDate when marking as done

                    if (!task.isRepeating()) {
                        task.setNextRepeatDate(null);
                    }

                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }



    // delete a task -- by id
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Scheduled(fixedRate = 3600000)
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

    private LocalDateTime calculateNextRepeatDate(int repeatInterval) {
        return LocalDateTime.now().plusDays(repeatInterval);
    }


}
