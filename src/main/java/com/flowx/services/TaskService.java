package com.flowx.services;

import com.flowx.models.Task;
import com.flowx.repositories.TaskRepository;
import com.flowx.models.User;
import com.flowx.repositories.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }


    public Task createTask(Task task, Long userId) {
        System.out.println("🔥 Incoming Task: " + task);

        // Fetch user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setCreatedBy(user); // Assign the user before saving

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
    public Optional<Task> getTaskById(Long task_id) {
        return taskRepository.findById(task_id);
    }

    // update a task -- by id
    public Task updateTask(Long task_id, Task newTaskData) {
        return taskRepository.findById(task_id)
                .map(task -> {
                    task.setTitle(newTaskData.getTitle());
                    task.setDescription(newTaskData.getDescription());
                    task.setPriority(newTaskData.getPriority());
                    task.setCompleted(newTaskData.isCompleted());
                    task.setUpdatedAt(LocalDateTime.now());

//                    // Preserve repeating status
//                    task.setRepeating(newTaskData.isRepeating());
//                    task.setRepeatInterval(newTaskData.getRepeatInterval());
//
//                    // 🔥 Always recalculate nextRepeatDate if task is repeating
//                    if (task.isRepeating() && task.getRepeatInterval() != null) {
//                        task.setNextRepeatDate(calculateNextRepeatDate(task.getRepeatInterval()));
//                    } else {
//                        // ❌ If repeating is turned off, clear nextRepeatDate
//                        task.setNextRepeatDate(null);
//                    }

                    // ✅ debug
                    System.out.println("📥 Incoming repeating: " + newTaskData.isRepeating());
                    System.out.println("📥 Incoming repeatInterval: " + newTaskData.getRepeatInterval());

                    // ✅ ensure repeating status updates properly
                    task.setRepeating(newTaskData.isRepeating());
                    task.setRepeatInterval(newTaskData.isRepeating() ? newTaskData.getRepeatInterval() : null);

                    // ✅ only set nextRepeatDate if it's a recurring task
                    if (task.isRepeating() && task.getRepeatInterval() != null) {
                        task.setNextRepeatDate(calculateNextRepeatDate(task.getRepeatInterval()));
                    } else {
                        task.setNextRepeatDate(null);
                    }

                    System.out.println("🔄 After update: " + task.isRepeating()); // Debugging
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }



    // put = save toggled task
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public Task toggleTaskCompletion(Long task_id) {
        return taskRepository.findById(task_id)
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
    public void deleteTask(Long task_id) {
        taskRepository.deleteById(task_id);
    }

    // reset recurring tasks ------------------
    @Scheduled(fixedRate = 3600000)
//    @Scheduled(fixedRate = 60000) // runs every minute for testing --- DEBUG ONLY
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


    @Transactional
    public int deleteCompletedTasksByUser(Long userId) {
        return taskRepository.deleteCompletedTasksByUser(userId); // Pass only the userId
    }




    private LocalDateTime calculateNextRepeatDate(int repeatInterval) {
        return LocalDateTime.now().plusDays(repeatInterval);
    }


}
