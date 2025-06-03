package com.example.hospitalemr.controller;

import com.example.hospitalemr.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nurse/stats")
public class NurseDashboardController {
    private final StatsService statsService;
    public NurseDashboardController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/all")
    public Map<String, Object> getAllStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("visits",     statsService.getDailyVisitCounts());
        result.put("topDrugs",   statsService.getTopPrescribedDrugs());
        result.put("ageGroups",  statsService.getAgeGroupDistribution());
        // 지난 7일 기간으로 기본 조회
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(6);
        result.put("heatmap", statsService.getHourlyVisitHeatmap(start, end));
        result.put("hourlyFocus", statsService.getVisitHourlyConcentration());
        return result;
    }
}
