package com.schedule.manager.service;

import com.schedule.manager.domain.*;
import com.schedule.manager.repository.NotificationRepository;
import com.schedule.manager.repository.ScheduleRepository;
import com.schedule.manager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public List<Notification> findAllByUser(String username) {
        User user = getUser(username);
        return notificationRepository.findByUserOrderByNotifiedAtDesc(user);
    }

    public List<Notification> findUnreadByUser(String username) {
        User user = getUser(username);
        return notificationRepository.findByUserAndReadFalseOrderByNotifiedAtDesc(user);
    }

    public long countUnread(String username) {
        User user = getUser(username);
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("알림을 찾을 수 없습니다."));
        notification.setRead(true);
    }

    @Transactional
    public void markAllAsRead(String username) {
        User user = getUser(username);
        notificationRepository.markAllAsReadByUser(user);
    }

    // 매일 오전 8시에 실행
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void checkAndCreateNotifications() {
        log.info("일정 알림 검사 시작: {}", LocalDateTime.now());

        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        // 3일 이내 마감 예정 일정 알림
        List<Schedule> upcomingSchedules = scheduleRepository
                .findByDueDateBetweenAndStatus(today, threeDaysLater, ScheduleStatus.PENDING);

        for (Schedule schedule : upcomingSchedules) {
            boolean alreadyNotified = notificationRepository.existsByScheduleAndTypeAndNotifiedAtAfter(
                    schedule, NotificationType.DUE_SOON, LocalDateTime.now().minusDays(1));

            if (!alreadyNotified) {
                long daysLeft = ChronoUnit.DAYS.between(today, schedule.getDueDate());
                String message = daysLeft == 0
                        ? String.format("'%s' 일정이 오늘 마감됩니다!", schedule.getTitle())
                        : String.format("'%s' 일정이 %d일 후 마감됩니다.", schedule.getTitle(), daysLeft);
                createNotification(schedule, NotificationType.DUE_SOON, message);
            }
        }

        // 마감 초과 일정 알림
        List<Schedule> overdueSchedules = scheduleRepository
                .findByDueDateBeforeAndStatus(today, ScheduleStatus.PENDING);

        for (Schedule schedule : overdueSchedules) {
            boolean alreadyNotified = notificationRepository.existsByScheduleAndTypeAndNotifiedAtAfter(
                    schedule, NotificationType.OVERDUE, LocalDateTime.now().minusDays(1));

            if (!alreadyNotified) {
                createNotification(schedule, NotificationType.OVERDUE,
                        String.format("'%s' 일정이 마감일을 초과했습니다. 확인이 필요합니다.", schedule.getTitle()));
            }
        }

        log.info("일정 알림 검사 완료");
    }

    @Transactional
    public void createNotification(Schedule schedule, NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setSchedule(schedule);
        notification.setUser(schedule.getUser());
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setNotifiedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
