package com.schedule.manager.controller;

import com.schedule.manager.domain.Priority;
import com.schedule.manager.domain.Schedule;
import com.schedule.manager.domain.ScheduleStatus;
import com.schedule.manager.dto.ScheduleDto;
import com.schedule.manager.service.NotificationService;
import com.schedule.manager.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final NotificationService notificationService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        String username = userDetails.getUsername();
        List<Schedule> schedules;

        if (keyword != null && !keyword.isBlank()) {
            schedules = scheduleService.searchByTitle(username, keyword);
            model.addAttribute("keyword", keyword);
        } else if (status != null && !status.isBlank()) {
            schedules = scheduleService.findByUserAndStatus(username, ScheduleStatus.valueOf(status));
            model.addAttribute("selectedStatus", status);
        } else {
            schedules = scheduleService.findAllByUser(username);
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("statuses", ScheduleStatus.values());
        model.addAttribute("unreadCount", notificationService.countUnread(username));
        return "schedule/list";
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("scheduleDto", new ScheduleDto());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("unreadCount", notificationService.countUnread(userDetails.getUsername()));
        model.addAttribute("isEdit", false);
        return "schedule/form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("scheduleDto") ScheduleDto dto,
            BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("isEdit", false);
            return "schedule/form";
        }

        scheduleService.create(dto, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "일정이 등록되었습니다.");
        return "redirect:/schedules";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        String username = userDetails.getUsername();
        model.addAttribute("schedule", scheduleService.findById(id, username));
        model.addAttribute("unreadCount", notificationService.countUnread(username));
        return "schedule/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        String username = userDetails.getUsername();
        Schedule schedule = scheduleService.findById(id, username);

        model.addAttribute("scheduleDto", ScheduleDto.from(schedule));
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", ScheduleStatus.values());
        model.addAttribute("scheduleId", id);
        model.addAttribute("unreadCount", notificationService.countUnread(username));
        model.addAttribute("isEdit", true);
        return "schedule/form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("scheduleDto") ScheduleDto dto,
            BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", ScheduleStatus.values());
            model.addAttribute("scheduleId", id);
            model.addAttribute("isEdit", true);
            return "schedule/form";
        }

        scheduleService.update(id, dto, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "일정이 수정되었습니다.");
        return "redirect:/schedules/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        scheduleService.delete(id, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "일정이 삭제되었습니다.");
        return "redirect:/schedules";
    }
}
