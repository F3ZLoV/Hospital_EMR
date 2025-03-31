package com.example.hospitalemr.controller;


import com.example.hospitalemr.domain.MedicalVisit;
import com.example.hospitalemr.repository.MedicalVisitRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/medicalvisit")
public class MedicalVisitController {
    private final MedicalVisitRepository visitRepository;
    private final MedicalVisitRepository medicalVisitRepository;

    public MedicalVisitController(MedicalVisitRepository visitRepository, MedicalVisitRepository medicalVisitRepository) {
        this.visitRepository = visitRepository;
        this.medicalVisitRepository = medicalVisitRepository;
    }

    @PostMapping("/create")
    @ResponseBody
    public Map<String, Object> createMedicalVisit(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitDate,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime visitTime,
            @RequestParam String visitReason,
            @RequestParam String visitType) {

        Map<String, Object> result = new HashMap<>();
        try {
            MedicalVisit medicalVisit = new MedicalVisit();
            medicalVisit.setPatientId(patientId);
            medicalVisit.setDoctorId(doctorId);
            medicalVisit.setVisitDate(visitDate);
            medicalVisit.setVisitTime(visitTime);
            medicalVisit.setVisitReason(visitReason);
            medicalVisit.setVisitType(visitType);

            medicalVisitRepository.save(medicalVisit);

            result.put("success", true);
            result.put("message", "진료 접수가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "진료 접수 등록에 실패하였습니다.");
        }
        return result;
    }
}
