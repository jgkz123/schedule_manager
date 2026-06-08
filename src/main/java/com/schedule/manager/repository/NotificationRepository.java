package com.schedule.manager.repository;

import com.schedule.manager.domain.Notification;
import com.schedule.manager.domain.NotificationType;
import com.schedule.manager.domain.Schedule;
import com.schedule.manager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndReadFalseOrderByNotifiedAtDesc(User user);

    List<Notification> findByUserOrderByNotifiedAtDesc(User user);

    long countByUserAndReadFalse(User user);

    boolean existsByScheduleAndTypeAndNotifiedAtAfter(
            Schedule schedule, NotificationType type, LocalDateTime dateTime);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user = :user")
    void markAllAsReadByUser(@Param("user") User user);
}
