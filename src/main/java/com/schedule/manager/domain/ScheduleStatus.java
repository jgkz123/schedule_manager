package com.schedule.manager.domain;

public enum ScheduleStatus {
    PENDING("대기중"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    CANCELLED("취소");

    private final String label;

    ScheduleStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
