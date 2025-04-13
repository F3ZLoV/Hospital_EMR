package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.CalendarEvent;
import com.example.hospitalemr.repository.CalendarEventRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarEventRepository calendarEventRepository;

    @Autowired
    public CalendarController(CalendarEventRepository calendarEventRepository) {
        this.calendarEventRepository = calendarEventRepository;
    }

    // 특정 월의 이벤트 조회 (예: /calendar/doctor/events?year=2025&month=3)
    @GetMapping("/{role}/events")
    public List<CalendarEvent> getEventsForMonth(
            @PathVariable String role,
            @RequestParam int year,
            @RequestParam int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return calendarEventRepository.findAllByEventDateBetweenAndRoleOrderByEventDateAsc(start, end, role);
    }

    // 생성 또는 수정 처리
    @PostMapping("/{role}/save")
    public Map<String, Object> saveEvent(
            @PathVariable String role,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) Integer eventId) {
        Map<String, Object> result = new HashMap<>();
        try {
            CalendarEvent event;
            if (eventId != null) {
                Optional<CalendarEvent> optionalEvent = calendarEventRepository.findById(eventId);
                if (optionalEvent.isPresent()) {
                    event = optionalEvent.get();
                } else {
                    result.put("success", false);
                    result.put("message", "일정을 찾을 수 없습니다. id=" + eventId);
                    return result;
                }
            } else {
                event = new CalendarEvent();
            }
            event.setEventDate(eventDate);
            event.setTitle(title);
            event.setDescription(description);
            event.setRole(role);
            calendarEventRepository.save(event);
            result.put("success", true);
            result.put("message", eventId != null ? "일정이 수정되었습니다." : "일정이 저장되었습니다.");
            result.put("eventId", event.getEventId());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "일정 저장에 실패하였습니다: " + e.getMessage());
        }
        return result;
    }

    // 삭제 처리 (예: /calendar/delete?eventId=123)
    @PostMapping("/delete")
    public Map<String, Object> deleteEvent(@RequestParam Integer eventId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Optional<CalendarEvent> optionalEvent = calendarEventRepository.findById(eventId);
            if (optionalEvent.isPresent()) {
                calendarEventRepository.delete(optionalEvent.get());
                result.put("success", true);
                result.put("message", "일정이 삭제되었습니다.");
            } else {
                result.put("success", false);
                result.put("message", "해당 id의 일정이 존재하지 않습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "삭제 중 오류: " + e.getMessage());
        }
        return result;
    }
}
