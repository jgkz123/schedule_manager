package com.schedule.manager.service;

import com.schedule.manager.domain.Schedule;
import com.schedule.manager.domain.ScheduleStatus;
import com.schedule.manager.domain.User;
import com.schedule.manager.dto.ScheduleDto;
import com.schedule.manager.repository.ScheduleRepository;
import com.schedule.manager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public List<Schedule> findAllByUser(String username) {
        User user = getUser(username);
        return scheduleRepository.findByUserOrderByDueDateAsc(user);
    }

    public List<Schedule> findByUserAndStatus(String username, ScheduleStatus status) {
        User user = getUser(username);
        return scheduleRepository.findByUserAndStatusOrderByDueDateAsc(user, status);
    }

    public List<Schedule> searchByTitle(String username, String keyword) {
        User user = getUser(username);
        return scheduleRepository.searchByTitle(user, keyword);
    }

    public Schedule findById(Long id, String username) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다."));
        if (!schedule.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        return schedule;
    }

    @Transactional
    public void create(ScheduleDto dto, String username) {
        User user = getUser(username);

        Schedule schedule = new Schedule();
        schedule.setTitle(dto.getTitle());
        schedule.setDescription(dto.getDescription());
        schedule.setStartDate(dto.getStartDate());
        schedule.setDueDate(dto.getDueDate());
        schedule.setPriority(dto.getPriority());
        schedule.setStatus(ScheduleStatus.PENDING);
        schedule.setUser(user);

        scheduleRepository.save(schedule);
    }

    @Transactional
    public void update(Long id, ScheduleDto dto, String username) {
        Schedule schedule = findById(id, username);
        schedule.setTitle(dto.getTitle());
        schedule.setDescription(dto.getDescription());
        schedule.setStartDate(dto.getStartDate());
        schedule.setDueDate(dto.getDueDate());
        schedule.setPriority(dto.getPriority());
        schedule.setStatus(dto.getStatus());
    }

    @Transactional
    public void delete(Long id, String username) {
        Schedule schedule = findById(id, username);
        scheduleRepository.delete(schedule);
    }

    public List<Schedule> findUpcomingSchedules(String username) {
        User user = getUser(username);
        LocalDate today = LocalDate.now();
        return scheduleRepository.findByUserAndDueDateBetweenAndStatusOrderByDueDateAsc(
                user, today, today.plusDays(3), ScheduleStatus.PENDING);
    }

    public List<Schedule> findOverdueSchedules(String username) {
        User user = getUser(username);
        return scheduleRepository.findByUserAndDueDateBeforeAndStatus(
                user, LocalDate.now(), ScheduleStatus.PENDING);
    }

    public long countByStatus(String username, ScheduleStatus status) {
        User user = getUser(username);
        return scheduleRepository.countByUserAndStatus(user, status);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
