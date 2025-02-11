package com.flowx.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // checks specific field
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;


@Entity // tells JPA that this is an entity and will be mapped to the db
@Table(name = "tasks") // maps this entity to the "tasks" table

public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    @NotBlank(message = "choose a title for your task")
    @Size(min = 3, max = 240, message = "title must be between 3 and 240 chars")
    @Column(nullable = false)
    private String title;

    @Size(max = 510, message = "description must be under 510 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean completed; // Java default: false

    // test-IN...
    @Column(nullable = false)
    private int priority; // store priority as an int


    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // CONSTRUCTORS
    public Task() {} // empty constructor for JPA

    public Task(String title, String description, int priorityValue) {
        this.title = title;
        this.description = description;
        this.completed = false;
        this.priority = priorityValue;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    // GETTERS & SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    // testing...
    public int getPriority() { return priority; }
    public void setPriority(int priorityValue) { this.priority = priorityValue; }


    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
