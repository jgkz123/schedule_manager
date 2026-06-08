package com.schedule.manager.domain;

public enum NotificationType {
    DUE_SOON("마감 임박"),
    OVERDUE("마감 초과"),
    REMINDER("리마인더");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
