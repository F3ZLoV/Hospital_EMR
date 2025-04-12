package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.Prescription;
import com.example.hospitalemr.repository.PrescriptionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionController(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @PostMapping("/save")
    public Map<String, Object> savePrescriptions(@RequestBody List<Prescription> prescriptions) {
        Map<String, Object> result = new HashMap<>();
        try {
            prescriptionRepository.saveAll(prescriptions);
            result.put("success", true);
            result.put("message", "처방이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "처방 저장에 실패하였습니다: " + e.getMessage());
        }
        return result;
    }
}
