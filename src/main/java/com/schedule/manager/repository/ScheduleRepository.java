package com.schedule.manager.repository;

import com.schedule.manager.domain.Schedule;
import com.schedule.manager.domain.ScheduleStatus;
import com.schedule.manager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByUserOrderByDueDateAsc(User user);

    List<Schedule> findByUserAndStatusOrderByDueDateAsc(User user, ScheduleStatus status);

    List<Schedule> findByUserAndDueDateBetweenAndStatusOrderByDueDateAsc(
            User user, LocalDate start, LocalDate end, ScheduleStatus status);

    List<Schedule> findByUserAndDueDateBeforeAndStatus(
            User user, LocalDate date, ScheduleStatus status);

    List<Schedule> findByDueDateBetweenAndStatus(
            LocalDate start, LocalDate end, ScheduleStatus status);

    List<Schedule> findByDueDateBeforeAndStatus(LocalDate date, ScheduleStatus status);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.user = :user AND s.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") ScheduleStatus status);

    @Query("SELECT s FROM Schedule s WHERE s.user = :user AND " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Schedule> searchByTitle(@Param("user") User user, @Param("keyword") String keyword);
}
