package com.schedule.manager.domain;

public enum Priority {
    LOW("낮음"),
    MEDIUM("보통"),
    HIGH("높음"),
    URGENT("긴급");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
