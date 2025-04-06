package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.CalendarEvent;
import com.example.hospitalemr.repository.CalendarEventRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarEventRepository calendarEventRepository;

    public CalendarController(CalendarEventRepository calendarEventRepository) {
        this.calendarEventRepository = calendarEventRepository;
    }

    // 특정 월의 이벤트 조회 (역할에 따라)
    @GetMapping("/{role}/events")
    @ResponseBody
    public List<CalendarEvent> getEventsForMonth(
            @PathVariable String role,
            @RequestParam int year,
            @RequestParam int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return calendarEventRepository.findAllByEventDateBetweenAndRoleOrderByEventDateAsc(start, end, role);
    }

    // 해당 날짜의 이벤트 생성/수정 (역할에 따라)
    @PostMapping("/{role}/save")
    @ResponseBody
    public Map<String, Object> saveEvent(
            @PathVariable String role,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
            @RequestParam String title,
            @RequestParam String description) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 같은 날짜, 같은 역할의 이벤트가 있으면 수정, 없으면 새로 생성
            CalendarEvent event = calendarEventRepository.findByEventDateAndRole(eventDate, role)
                    .orElse(new CalendarEvent());
            event.setEventDate(eventDate);
            event.setTitle(title);
            event.setDescription(description);
            event.setRole(role);
            calendarEventRepository.save(event);
            result.put("success", true);
            result.put("message", "일정이 저장되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "일정 저장에 실패하였습니다.");
        }
        return result;
    }
}
