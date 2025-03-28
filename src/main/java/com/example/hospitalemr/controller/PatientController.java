package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.repository.PatientRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            result.put("message", "환자 등록에 성공하였습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "환자 등록에 실패하였습니다.");
        }
        return result;
    }

    //환자 접수
    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> registerPatient(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository
                    .findById(patientId).orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));

            if(patient.isWaiting()) {
                result.put("success", false);
                result.put("message", "이미 접수된 환자입니다.");
                return result;
            }

            patient.setWaiting(true);
            patient.setReceptionTime(LocalDateTime.now());
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "환자 접수에 성공하였습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "환자 접수에 실패하였습니다.");
        }
        return result;
    }




    @GetMapping("/search")
    public String searchPatients(@RequestParam String keyword, Model model) {
        List<Patient> allPatients = patientRepository.findAll();

        List<Patient> filtered = allPatients.stream()
                .filter(p -> {
                    if (p.getName() != null && p.getName().contains(keyword)) return true;
                    if (p.getDate_of_birth() != null &&
                            p.getDate_of_birth().toString().contains(keyword)) return true;
                    if (p.getPhone_number() != null && p.getPhone_number().contains(keyword)) return true;
                    return false;
                })
                .collect(Collectors.toList());

        model.addAttribute("patients", filtered);
        return "search_result :: resultFragment";
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

    // 환자 접수 후 대기 리스트에 올림
    @GetMapping("/waiting")
    public String getWaitingPatients(Model model) {
        List<Patient> waitingPatients = patientRepository.findAll().stream()
                .filter(Patient::isWaiting)
                .sorted(Comparator.comparing(Patient::getReceptionTime)) // 등록 시간이 빠른 순서대로 정렬
                .collect(Collectors.toList());
        model.addAttribute("patients", waitingPatients);
        return "waiting_list :: waitingFragment";
    }

    // 환자 접수 대기 리스트에서 환자 삭제
    @PostMapping("/waiting/remove")
    @ResponseBody
    public Map<String, Object> removeFromWaiting(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
            patient.setWaiting(false);
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "대기 리스트에서 제거되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "대기 리스트에서 제거에 실패하였습니다.");
        }
        return result;
    }

    // 접수 통계 집계
    @GetMapping("/stats")
    @ResponseBody
    public Map<String, Object> getPatientStats() {
        Map<String, Object> stats = new HashMap<>();
        long waitingCount = patientRepository.countByWaiting(true);
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        long collectionCount = patientRepository.countByWaitingFalseAndReceptionTimeBetween(start, end);
        stats.put("waitingCount", waitingCount);
        stats.put("collectionCount", collectionCount);
        return stats;
    }


    // 5) 환자 삭제
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientRepository.deleteById(id);
    }

}