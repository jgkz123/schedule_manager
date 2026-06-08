package com.schedule.manager.dto;

import com.schedule.manager.domain.Priority;
import com.schedule.manager.domain.Schedule;
import com.schedule.manager.domain.ScheduleStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
public class ScheduleDto {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String title;

    @Size(max = 2000, message = "내용은 2000자 이내로 입력해주세요.")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "마감일을 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @NotNull(message = "우선순위를 선택해주세요.")
    private Priority priority = Priority.MEDIUM;

    private ScheduleStatus status = ScheduleStatus.PENDING;

    public static ScheduleDto from(Schedule schedule) {
        ScheduleDto dto = new ScheduleDto();
        dto.setTitle(schedule.getTitle());
        dto.setDescription(schedule.getDescription());
        dto.setStartDate(schedule.getStartDate());
        dto.setDueDate(schedule.getDueDate());
        dto.setPriority(schedule.getPriority());
        dto.setStatus(schedule.getStatus());
        return dto;
    }
}
