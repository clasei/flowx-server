package com.flowx.controllers;

import com.flowx.models.Task;
import com.flowx.services.TaskService;
import jakarta.validation.Valid; // validates the whole object = task data
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // flexible HTTP responses: lets return data & status codes
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*; // all annotations included: e.g. @RestController, @RequestMapping
import org.springframework.web.bind.annotation.CrossOrigin; // allows requests from any origin

import java.util.List;
import java.util.Optional; // avoids null checks
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused") // just to avoid unused warnings
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
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
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Task> toggleTaskCompletion(@PathVariable Long id) {
        Optional<Task> taskOptional = taskService.getTaskById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setCompleted(!task.isCompleted()); // toggle true/false
            task.setUpdatedAt(java.time.LocalDateTime.now()); // update timestamp

            Task updatedTask = taskService.updateTask(id, task); // ensure persistence

            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
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