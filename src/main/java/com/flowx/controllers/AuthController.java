package com.flowx.controllers;

import com.flowx.models.User;
import com.flowx.repositories.UserRepository;

import com.flowx.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");

        // Check if user exists
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User already exists"));
        }

        // Save new user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // TODO: Hash password later !!!!
        userRepository.save(newUser);

        // Generate token
        String token = jwtUtil.generateToken(username, "user");

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getUsername(), "user");

        return ResponseEntity.ok(Map.of("token", token));
    }

//    @PostMapping("/signup")
//    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {
//        String username = request.get("username");
//        String password = request.get("password"); // store in the db
//        String role = "user"; // Default role for new users
//
//        // simulate an automatic login
//        String token = jwtUtil.generateToken(username, role);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("token", token);
//
//        return ResponseEntity.ok(response); // signup returns a valid login token
//    }
//
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
//        String username = request.get("username");
//        String role = "user"; // Set role (for simplicity)
//
//        String token = jwtUtil.generateToken(username, role);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("token", token);
//
//        return ResponseEntity.ok(response);
//    }

}
