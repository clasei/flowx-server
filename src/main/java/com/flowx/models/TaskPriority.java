package com.flowx.models;

public enum TaskPriority {
    CRITICAL(3),
    FOCUS(2),
    PIPELINE(1);

    private final int level;

    TaskPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static TaskPriority fromLevel(int level) {
        for (TaskPriority priority : TaskPriority.values()) {
            if (priority.getLevel() == level) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority level: " + level);
    }
}
