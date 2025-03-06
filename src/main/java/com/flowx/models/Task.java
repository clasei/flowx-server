package com.flowx.models;

//import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // checks specific field
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity // tells JPA that this is an entity and will be mapped to the db
@Table(name = "tasks") // maps this entity to the "tasks" table

public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id") // Rename column for clarity
    private Long task_id; // Primary Key

    @NotBlank(message = "choose a title for your task")
    @Size(min = 3, max = 120, message = "title must be between 3 and 120 chars")
    @Column(nullable = false)
    private String title;

    @Size(max = 490, message = "description must be under 490 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean completed = false; // Java default: false

    @Column(nullable = false)
    private int priority; // store priority as an int

    @Column(updatable = false) // Prevents updates
    private LocalDateTime createdAt;

    @Column(nullable = false) // Ensure updates are stored
    private LocalDateTime updatedAt;

    // new feature: recurring tasks
    @JsonProperty("repeating") // ensures JSON maps this as "repeating"
    @Column(nullable = false)
    private boolean repeating = false;

    @Column
    private Integer repeatInterval; // Days

    @Column
    private LocalDateTime nextRepeatDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // update foreign key
//    @JsonBackReference // prevents infinite recursion
    private User createdBy; // store the user who made the task

    // CONSTRUCTORS
    public Task() {
        // jpa requires a no-argument constructor
        // without it Hibernate (jpa) cannot instantiate objects when running GET queries aka fetching data
    }

    public Task(String title, String description, int priority, boolean repeating, Integer repeatInterval, LocalDateTime nextRepeatDate, User createdBy) {
        this.title = title;
        this.description = description;
        this.completed = false;
        this.priority = priority;
        this.repeating = repeating;
        this.repeatInterval = repeatInterval;
        this.nextRepeatDate = nextRepeatDate;
        this.createdBy = createdBy;
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
    public Long getTask_id() { return task_id; }  // Match field name
    public void setTask_id(Long task_id) { this.task_id = task_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getPriority() { return priority; }
    public void setPriority(int priorityValue) { this.priority = priorityValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ----------- is repeating ----- issues solved
    @JsonProperty("repeating") // ensures JSON correctly maps "repeating"
    public boolean isRepeating() { return repeating; }
    @JsonProperty("repeating") // ensures JSON correctly maps "repeating"
    public void setRepeating(boolean repeating) { this.repeating = repeating; }

    public Integer getRepeatInterval() { return repeatInterval; }
    public void setRepeatInterval(Integer repeatInterval) { this.repeatInterval = repeatInterval; }

    public LocalDateTime getNextRepeatDate() { return nextRepeatDate; }
    public void setNextRepeatDate(LocalDateTime nextRepeatDate) { this.nextRepeatDate = nextRepeatDate; }

    public User getCreatedBy() { return createdBy; }  // fetch associated user
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

}
