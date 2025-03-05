package com.flowx.models;

import jakarta.persistence.*;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users") // table name

public class User {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id; // Unique user ID
//
//    @Column(nullable = false, unique = true)
//    private String username; // Unique username
//
//    @Column(nullable = false, unique = true)
//    private String email; // Unique email
//
//    @Column(nullable = false)
//    private String password; // Store hashed password (later)

//
//    @Column(nullable = false)
//    private String role = "user"; // Default role

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // Rename column
    private Long user_id; // user ID

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Store hashed password -- ADD THIS later !!!!

    @Column(nullable = false)
    private String role = "user"; // Default role -- user, admin...

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>(); // relation user - task


    // GETTERS & SETTERS
    public Long getUser_id() { return user_id; }  // Match field name
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<Task> getTasks() { return tasks; }  // Keep this for fetching user tasks
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
}
