package com.example.hospitalemr.controller;

import com.example.hospitalemr.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
        result.put("visits", statsService.getDailyVisitCounts());
        result.put("topDrugs", statsService.getTopPrescribedDrugs());
        result.put("ageGroups", statsService.getAgeGroupDistribution());
        return result;
    }
}
