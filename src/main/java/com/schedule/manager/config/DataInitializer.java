package com.schedule.manager.config;

import com.schedule.manager.domain.*;
import com.schedule.manager.repository.NotificationRepository;
import com.schedule.manager.repository.ScheduleRepository;
import com.schedule.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final NotificationRepository notificationRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername("demo")) {
            return;
        }

        // 데모 계정 생성
        User user = new User();
        user.setUsername("demo");
        user.setEmail("demo@example.com");
        user.setPassword(passwordEncoder.encode("demo1234"));
        user.setRole(Role.USER);
        userRepository.save(user);

        // 샘플 일정 생성
        createSchedule(user, "기말 프로젝트 제출", "Spring Boot 일정 관리 시스템 구현 및 보고서 작성",
                LocalDate.now().minusDays(3), LocalDate.now().plusDays(7), Priority.URGENT, ScheduleStatus.IN_PROGRESS);

        createSchedule(user, "알고리즘 과제", "동적 프로그래밍 문제 풀이 5문제",
                LocalDate.now(), LocalDate.now().plusDays(2), Priority.HIGH, ScheduleStatus.PENDING);

        createSchedule(user, "데이터베이스 시험 준비", "SQL, 정규화, 인덱스 개념 복습",
                LocalDate.now(), LocalDate.now().plusDays(1), Priority.HIGH, ScheduleStatus.PENDING);

        createSchedule(user, "독서 클럽 모임 준비", "이번 달 도서 요약 및 토론 주제 준비",
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(5), Priority.MEDIUM, ScheduleStatus.PENDING);

        createSchedule(user, "운동 루틴 정착", "주 3회 헬스장 방문 목표",
                LocalDate.now().minusDays(7), LocalDate.now().plusDays(14), Priority.LOW, ScheduleStatus.IN_PROGRESS);

        Schedule overdueSchedule = createSchedule(user, "영어 에세이 제출", "토플 준비 영어 에세이 작성",
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(2), Priority.HIGH, ScheduleStatus.PENDING);

        createSchedule(user, "팀 프레젠테이션 준비", "캡스톤 디자인 중간 발표 자료 제작",
                LocalDate.now(), LocalDate.now().plusDays(10), Priority.MEDIUM, ScheduleStatus.PENDING);

        Schedule completedSchedule = createSchedule(user, "기말 보고서 초안 작성", "서론 및 관련 연구 섹션 완성",
                LocalDate.now().minusDays(14), LocalDate.now().minusDays(3), Priority.HIGH, ScheduleStatus.COMPLETED);

        // 샘플 알림 생성
        Notification n1 = new Notification();
        n1.setUser(user);
        n1.setSchedule(overdueSchedule);
        n1.setMessage("'영어 에세이 제출' 일정이 마감일을 초과했습니다. 확인이 필요합니다.");
        n1.setType(NotificationType.OVERDUE);
        n1.setRead(false);
        n1.setNotifiedAt(LocalDateTime.now().minusHours(2));
        notificationRepository.save(n1);

        Notification n2 = new Notification();
        n2.setUser(user);
        n2.setSchedule(scheduleRepository.findByUserOrderByDueDateAsc(user).get(1));
        n2.setMessage("'알고리즘 과제' 일정이 2일 후 마감됩니다.");
        n2.setType(NotificationType.DUE_SOON);
        n2.setRead(false);
        n2.setNotifiedAt(LocalDateTime.now().minusHours(1));
        notificationRepository.save(n2);

        log.info("샘플 데이터 초기화 완료 - 데모 계정: demo / demo1234");
    }

    private Schedule createSchedule(User user, String title, String description,
                                     LocalDate startDate, LocalDate dueDate,
                                     Priority priority, ScheduleStatus status) {
        Schedule schedule = new Schedule();
        schedule.setUser(user);
        schedule.setTitle(title);
        schedule.setDescription(description);
        schedule.setStartDate(startDate);
        schedule.setDueDate(dueDate);
        schedule.setPriority(priority);
        schedule.setStatus(status);
        return scheduleRepository.save(schedule);
    }
}
