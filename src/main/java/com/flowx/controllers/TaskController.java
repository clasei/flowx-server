package com.flowx.controllers;

import com.flowx.models.Task;
import com.flowx.services.TaskService;
import com.flowx.repositories.TaskRepository;
import jakarta.validation.Valid; // validates the whole object = task data
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // flexible HTTP responses: lets return data & status codes
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*; // all annotations included: e.g. @RestController, @RequestMapping
import org.springframework.web.bind.annotation.CrossOrigin; // allows requests from any origin
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional; // avoids null checks
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused") // just to avoid unused warnings
@CrossOrigin(origins = "http://localhost:4200") // check before deployment
@RestController
@RequestMapping("/tasks")

public class TaskController {

    private final TaskService taskService; // inject the repository == define task-service (already imported)
    private final TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService; // assign the repository
        this.taskRepository = taskRepository;
    }

    // GET all tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    // GET a single task by id
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST a new task
    @PostMapping
    public Task createTask(@Valid @RequestBody Task task) {
        return taskService.createTask(task);
    }

    // PUT (update) a task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task taskData) {
        try {
            Task updatedTask = taskService.updateTask(id, taskData);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT (toggle completion status)
//    @PutMapping("/{id}/toggle")
//    public ResponseEntity<Task> toggleTaskCompletion(@PathVariable Long id) {
//        Optional<Task> taskOptional = taskService.getTaskById(id);
//        if (taskOptional.isPresent()) {
//            Task task = taskOptional.get();
//            task.setCompleted(!task.isCompleted()); // toggle true/false
//            task.setUpdatedAt(java.time.LocalDateTime.now()); // update timestamp
//
//            Task updatedTask = taskService.updateTask(id, task); // ensure persistence
//
//            return ResponseEntity.ok(updatedTask);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    // new feature: recurring tasks -- test
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Task> toggleTaskCompletion(@PathVariable Long id) {
        try {
            Task updatedTask = taskService.toggleTaskCompletion(id);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // DELETE a task by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete task: " + e.getMessage());
        }
    }


    // DELETE all completed tasks

//    @DeleteMapping("/completed")
//    public ResponseEntity<?> deleteAllCompletedTasks() {
//        try {
//            int deletedCount = taskRepository.deleteByCompleted();
//            return ResponseEntity.ok("Deleted " + deletedCount + " completed tasks.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete completed tasks.");
//        }
//    }

    @DeleteMapping("/completed")
    public ResponseEntity<Map<String, Object>> deleteAllCompletedTasks() {
        Map<String, Object> response = new HashMap<>();
        try {
            int deletedCount = taskService.deleteAllCompletedTasks();
            response.put("message", "Deleted " + deletedCount + " completed tasks.");
            response.put("status", "success");
            return ResponseEntity.ok(response); // ✅ JSON response
        } catch (Exception e) {
            response.put("message", "Failed to delete completed tasks.");
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST) // return 400 if validation fails
    @ExceptionHandler(MethodArgumentNotValidException.class) // catch validation errors
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>(); // initialize errors map

        ex.getBindingResult().getAllErrors().forEach((error) -> { // loops through validation errors
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage); // store errors
        });
        return errors; // returns errors as a JSON response
    }
}