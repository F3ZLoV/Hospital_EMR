package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Integer> {
    Optional<CalendarEvent> findByEventDateAndRole(LocalDate eventDate, String role);
    List<CalendarEvent> findAllByEventDateBetweenAndRoleOrderByEventDateAsc(LocalDate start, LocalDate end, String role);
    CalendarEvent findByRoleAndEventDate(String role, LocalDate eventDate);
}
