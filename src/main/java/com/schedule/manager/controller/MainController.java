package com.schedule.manager.controller;

import com.schedule.manager.domain.ScheduleStatus;
import com.schedule.manager.service.NotificationService;
import com.schedule.manager.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ScheduleService scheduleService;
    private final NotificationService notificationService;

    @GetMapping("/")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();

        model.addAttribute("upcomingSchedules", scheduleService.findUpcomingSchedules(username));
        model.addAttribute("overdueSchedules", scheduleService.findOverdueSchedules(username));
        model.addAttribute("unreadNotifications", notificationService.findUnreadByUser(username));
        model.addAttribute("unreadCount", notificationService.countUnread(username));
        model.addAttribute("pendingCount", scheduleService.countByStatus(username, ScheduleStatus.PENDING));
        model.addAttribute("inProgressCount", scheduleService.countByStatus(username, ScheduleStatus.IN_PROGRESS));
        model.addAttribute("completedCount", scheduleService.countByStatus(username, ScheduleStatus.COMPLETED));
        model.addAttribute("username", username);

        return "index";
    }
}
