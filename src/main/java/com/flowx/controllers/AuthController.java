package com.flowx.controllers;

import com.flowx.models.User;
import com.flowx.repositories.UserRepository;

import com.flowx.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;


import jakarta.validation.Valid;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200") // CHECK this later !!!!
@RestController
@RequestMapping("/auth")
public class AuthController {

    // PasswordEncoder instance
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User newUser) {  // ✅ USAMOS `User` directamente
        if (userRepository.findByEmail(newUser.getEmail()).isPresent() || userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "user already exists"));
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));  // ✅ Hash password
        userRepository.save(newUser);

        String token = jwtUtil.generateToken(newUser.getUsername(), "user");

        return ResponseEntity.ok(Map.of("token", token));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "did you forget your email?"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "that's not your password"));
        }

        String token = jwtUtil.generateToken(user.getUsername(), "user");
        System.out.println("Generated Token: " + token);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername()
        ));
    }

}
