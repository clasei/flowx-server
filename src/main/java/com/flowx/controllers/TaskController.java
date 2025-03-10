package com.flowx.controllers;

import com.flowx.models.Task;
import com.flowx.repositories.TaskRepository;
import com.flowx.models.User;
import com.flowx.repositories.UserRepository;
import com.flowx.services.TaskService;

import jakarta.validation.Valid; // validates the whole object = task data
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // flexible HTTP responses: lets return data & status codes
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*; // all annotations included: e.g. @RestController, @RequestMapping
import org.springframework.web.bind.annotation.CrossOrigin; // allows requests from any origin
import org.springframework.beans.factory.annotation.Autowired;

//import java.util.List;
//import java.util.Optional; // avoids null checks
//import java.util.HashMap;
//import java.util.Map;
import java.util.*;

import com.flowx.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("unused") // just to avoid unused warnings
@CrossOrigin(origins = "http://localhost:4200") // CHECK before deployment !!!!
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService; // inject the repository == define task-service (already imported)
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public TaskController(TaskService taskService, TaskRepository taskRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.taskService = taskService; // assign the repository
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // GET USER TASKS
    @GetMapping
    public ResponseEntity<?> getUserTasks(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Claims claims = jwtUtil.validateToken(token);
            String username = claims.getSubject();

            // Find the user
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body("User not found");
            }
            User user = userOpt.get();

            // Fetch only tasks for this user
            List<Task> tasks = taskRepository.findByCreatedBy(user);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }

//    // GET all tasks
//    @GetMapping
//    public List<Task> getAllTasks() {
//        return taskService.getAllTasks();
//    }

    // GET all tasks (Protected: Requires JWT token)

//    @GetMapping
//    public ResponseEntity<?> getAllTasks(@RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = authHeader.replace("Bearer ", ""); // Remove Bearer prefix
//            Claims claims = jwtUtil.validateToken(token); // Validate token
//            String username = claims.getSubject(); // Extract username from token
//
//            // Log the user accessing the tasks
//            System.out.println("User " + username + " accessed tasks.");
//
//            // Return tasks
//            List<Task> tasks = taskService.getAllTasks();
//            return ResponseEntity.ok(tasks);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
//        }
//    }

//    @GetMapping
//    public ResponseEntity<List<Task>> getAllTasks() {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println("User " + userDetails.getUsername() + " accessed tasks.");
//
//        return ResponseEntity.ok(taskService.getAllTasks());
//    }

    // GET a single task by id
//    @GetMapping("/{id}")
//    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
//        Optional<Task> task = taskService.getTaskById(id);
//        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }

    @GetMapping("/{task_id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long task_id, @RequestHeader("Authorization") String authHeader) {
        try {
            // ✅ Extract the username from the JWT token
            String token = authHeader.replace("Bearer ", "");
            Claims claims = jwtUtil.validateToken(token);
            String username = claims.getSubject();
            System.out.println("User extracted from token: " + username);

            // ✅ Find the user in the database
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                System.out.println("User not found!");
                return ResponseEntity.status(401).body("Unauthorized");
            }
            User user = userOpt.get();

            // ✅ Find the task by ID
            Optional<Task> taskOpt = taskService.getTaskById(task_id);
            if (taskOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Task not found");
            }
            Task task = taskOpt.get();

            // ✅ Ensure the user is the owner of the task
            if (!task.getCreatedBy().getUser_id().equals(user.getUser_id())) {
                return ResponseEntity.status(403).body("You don't have permission to access this task");
            }

            // ✅ Return the task if everything checks out
            return ResponseEntity.ok(task);

        } catch (Exception e) {
            System.out.println("Error in GET /tasks/" + task_id + ": " + e.getMessage());
            return ResponseEntity.status(403).body("Invalid or expired token");
        }
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println("User " + userDetails.getUsername() + " accessed task " + id);
//
//        return taskService.getTaskById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }


    // POST a new task
//    @PostMapping
//    public Task createTask(@Valid @RequestBody Task task) {
//        return taskService.createTask(task);
//    }

//    @PostMapping
//    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println("User " + userDetails.getUsername() + " is creating a task.");
//
//        return ResponseEntity.ok(taskService.createTask(task));
//    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("Received token: " + authHeader);

            // Extract token
            String token = authHeader.replace("Bearer ", "");
            Claims claims = jwtUtil.validateToken(token);
            String username = claims.getSubject();
            System.out.println("User extracted from token: " + username);

            // Find the user in the database
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                System.out.println("User not found!");
                return ResponseEntity.status(401).body(null);
            }
            User user = userOpt.get();

            // Set the user in the task
            task.setCreatedBy(user);

            Task createdTask = taskService.createTask(task, user.getUser_id());
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            System.out.println("Error in POST /tasks: " + e.getMessage());
            return ResponseEntity.status(403).body(null);
        }
    }



//    @PostMapping("/tasks/{userId}")
//    public ResponseEntity<Task> createTask(@PathVariable Long userId, @RequestBody Task task) {
//        Task createdTask = taskService.createTask(task, userId);
//        return ResponseEntity.ok(createdTask);
//    }


    // PUT (update) a task
//    @PutMapping("/{id}")
//    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task taskData) {
//        try {
//            Task updatedTask = taskService.updateTask(id, taskData);
//            return ResponseEntity.ok(updatedTask);
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @PutMapping("/{task_id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long task_id, @Valid @RequestBody Task taskData) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("User " + userDetails.getUsername() + " is updating task " + task_id);

        try {
            // ✅ Find the task
            Optional<Task> taskOpt = taskService.getTaskById(task_id);
            if (taskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Task existingTask = taskOpt.get();

            // ✅ Check if the logged-in user is the task owner
            if (!existingTask.getCreatedBy().getUsername().equals(userDetails.getUsername())) {
                return ResponseEntity.status(403).body(null); // 🔒 Forbidden if it's not their task
            }

            // ✅ Only update non-null fields
            if (taskData.getTitle() != null) existingTask.setTitle(taskData.getTitle());
            if (taskData.getDescription() != null) existingTask.setDescription(taskData.getDescription());
            if (taskData.getPriority() != 0) existingTask.setPriority(taskData.getPriority());
            if (taskData.getNextRepeatDate() != null) existingTask.setNextRepeatDate(taskData.getNextRepeatDate());
            if (taskData.getRepeatInterval() != null) existingTask.setRepeatInterval(taskData.getRepeatInterval());

//            if (taskData.getRepeating() != null) {
//                existingTask.setRepeating(taskData.getRepeating());
//            }

            existingTask.setRepeating(taskData.isRepeating());

            existingTask.setUpdatedAt(java.time.LocalDateTime.now()); // ✅ Update timestamp

            // ✅ Save and return updated task
            Task updatedTask = taskService.updateTask(task_id, existingTask);
            return ResponseEntity.ok(updatedTask);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{task_id}/toggle")
    public ResponseEntity<Task> toggleTaskCompletion(@PathVariable Long task_id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("User " + userDetails.getUsername() + " is toggling completion for task " + task_id);

        try {
            // ✅ Find the task
            Optional<Task> taskOpt = taskService.getTaskById(task_id);
            if (taskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Task existingTask = taskOpt.get();

            // ✅ Check if the logged-in user is the task owner
            if (!existingTask.getCreatedBy().getUsername().equals(userDetails.getUsername())) {
                return ResponseEntity.status(403).body(null); // 🔒 Forbidden if it's not their task
            }

            // ✅ Toggle completion
            existingTask.setCompleted(!existingTask.isCompleted());
            existingTask.setUpdatedAt(java.time.LocalDateTime.now()); // ✅ Update timestamp

            // ✅ Save and return updated task
            Task updatedTask = taskService.updateTask(task_id, existingTask);
            return ResponseEntity.ok(updatedTask);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{task_id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long task_id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("User " + userDetails.getUsername() + " is attempting to delete task " + task_id);

        try {
            // ✅ Find the task first
            Optional<Task> taskOpt = taskService.getTaskById(task_id);
            if (taskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Task existingTask = taskOpt.get();

            // ✅ Check if the logged-in user is the owner
            if (!existingTask.getCreatedBy().getUsername().equals(userDetails.getUsername())) {
                return ResponseEntity.status(403).body("You are not authorized to delete this task."); // 🔒 Forbidden
            }

            // ✅ Delete the task
            taskService.deleteTask(task_id);
            System.out.println("Task " + task_id + " deleted successfully by " + userDetails.getUsername());
            return ResponseEntity.noContent().build(); // ✅ 204 No Content (successful delete)

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete task: " + e.getMessage());
        }
    }


//    @DeleteMapping("/completed")
//    public ResponseEntity<Map<String, Object>> deleteAllCompletedTasks() {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println("User " + userDetails.getUsername() + " is attempting to delete all completed tasks.");
//
//        Map<String, Object> response = new HashMap<>();
//        try {
//            // ✅ Get the logged-in user
//            String username = userDetails.getUsername();
//            Optional<User> userOpt = userRepository.findByUsername(username);
//
//            if (userOpt.isEmpty()) {
//                response.put("message", "User not found.");
//                response.put("status", "error");
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//            }
//
//            User user = userOpt.get();
//
//            // ✅ Delete only the completed tasks **owned by this user**
////            int deletedCount = taskService.deleteCompletedTasksByUser(user);
//
//            int deletedCount = taskService.deleteCompletedTasksByUser(user.getUser_id());
//
//            response.put("message", "Deleted " + deletedCount + " completed tasks.");
//            response.put("status", "success");
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            response.put("message", "Failed to delete completed tasks.");
//            response.put("status", "error");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }

@DeleteMapping("/completed")
public ResponseEntity<Map<String, Object>> deleteAllCompletedTasks() {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    System.out.println("User " + userDetails.getUsername() + " is attempting to delete all completed tasks.");

    Map<String, Object> response = new HashMap<>();
    try {
        String username = userDetails.getUsername();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            response.put("message", "User not found.");
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userOpt.get();

        int deletedCount = taskService.deleteCompletedTasksByUser(user.getUser_id());

        response.put("message", "Deleted " + deletedCount + " completed tasks.");
        response.put("status", "success");
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        response.put("message", "Failed to delete completed tasks.");
        response.put("status", "error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

    // --------------------- reactivate repeating tasks --------- TEST & DEBUG
    @PutMapping("/tasks/reactivate")
    public ResponseEntity<String> reactivateRepeatingTasks() {
        int count = taskService.reactivateRepeatingTasks();
        return ResponseEntity.ok("Reactivated " + count + " tasks.");
    }
    // ---------------------------------------------


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