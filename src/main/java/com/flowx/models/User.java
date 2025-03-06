package com.flowx.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

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
    @Size(min = 3, max = 21, message = "your username must be between 3 and 21 characters")
    private String username;

    @Column(nullable = false, unique = true)
    @Email(message = "that doesn't look like an email")
    private String email;

    @NotBlank(message = "hey, you really need a password")
    @Size(min = 7, message = "you need more than 7 characters to build a password")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{7,}$",
            message = "make your password safe: 1 uppercase, 1 lowercase, and 1 number — min")
    private String password;

//    @Column(nullable = false)
//    private String password; // Store hashed password -- CHECK THIS later !!!! --> on it

    @Column(nullable = false)
    private String role = "user"; // Default role -- user, admin...

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // controls serialization, prevents infinite recurison
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
