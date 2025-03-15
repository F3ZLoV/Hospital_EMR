package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // 환자 조회
    @GetMapping
    public List<Patient> getAllPatient() {
        return patientRepository.findAll();
    }

    // 환자 등록
    @PostMapping
    @ResponseBody
    public Map<String, Object> createPatient(@ModelAttribute Patient patient) {
        Map<String, Object> result = new HashMap<>();
        try {
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "환자등록에 성공하였습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "환자등록에 실패하였습니다.");
        }
        return result;
    }

    // 환자 단건 조회
    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + id));
    }

    // 환자 정보 수정
    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @RequestBody Patient updateData) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + id));

        patient.setName(updateData.getName());
        patient.setPhone_number(updateData.getPhone_number());
        return patientRepository.save(patient);
    }

    // 5) 환자 삭제
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientRepository.deleteById(id);
    }

}
