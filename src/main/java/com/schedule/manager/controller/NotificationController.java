package com.schedule.manager.controller;

import com.schedule.manager.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        model.addAttribute("notifications", notificationService.findAllByUser(username));
        model.addAttribute("unreadCount", notificationService.countUnread(username));
        return "notification/list";
    }

    @PostMapping("/{id}/read")
    public String markAsRead(
            @PathVariable Long id,
            @RequestHeader(value = "Referer", defaultValue = "/notifications") String referer) {
        notificationService.markAsRead(id);
        return "redirect:" + referer;
    }

    @PostMapping("/read-all")
    public String markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUsername());
        return "redirect:/notifications";
    }
}
