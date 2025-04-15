package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.TreatmentPlan;
import com.example.hospitalemr.repository.TreatmentPlanRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/treatmentplan")
public class TreatmentPlanController {

    private final TreatmentPlanRepository treatmentPlanRepository;

    public TreatmentPlanController(TreatmentPlanRepository treatmentPlanRepository) {
        this.treatmentPlanRepository = treatmentPlanRepository;
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> saveTreatmentPlan(@RequestParam Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            TreatmentPlan tp = new TreatmentPlan();
            tp.setDiagnosisId(Integer.parseInt(params.get("diagnosisId")));
            tp.setTreatmentDescription(params.get("treatmentDescription"));

            if (params.containsKey("startDate") && !params.get("startDate").isEmpty()) {
                tp.setStartDate(LocalDate.parse(params.get("startDate")));
            }
            if (params.containsKey("endDate") && !params.get("endDate").isEmpty()) {
                tp.setEndDate(LocalDate.parse(params.get("endDate")));
            }
            tp.setStatus(params.get("status"));

            treatmentPlanRepository.save(tp);
            result.put("success", true);
            result.put("message", "치료 계획 저장 성공");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "치료 계획 저장 실패: " + e.getMessage());
        }
        return result;
    }
}
