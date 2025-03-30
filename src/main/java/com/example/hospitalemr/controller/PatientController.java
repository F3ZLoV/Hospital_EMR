package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.repository.PatientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // 환자 전체 조회
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

    // 환자 접수
    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> registerPatient(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));

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

    // 환자 검색
    @GetMapping("/search")
    public String searchPatients(@RequestParam String keyword, Model model) {
        List<Patient> allPatients = patientRepository.findAll();

        List<Patient> filtered = allPatients.stream()
                .filter(p -> {
                    if (p.getName() != null && p.getName().contains(keyword)) return true;
                    if (p.getDate_of_birth() != null && p.getDate_of_birth().toString().contains(keyword)) return true;
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

    // 대기 환자 리스트 (일반)
    @GetMapping("/waiting")
    public String getWaitingPatients(Model model) {
        List<Patient> waitingPatients = patientRepository.findAll().stream()
                .filter(Patient::isWaiting)
                .sorted(Comparator.comparing(Patient::getReceptionTime))
                .collect(Collectors.toList());
        model.addAttribute("patients", waitingPatients);
        return "waiting_list :: waitingFragment";
    }

    // 대기 환자 리스트 (의사용)
    @GetMapping("/doctor/waiting")
    public String getDoctorWaitingPatients(Model model) {
        List<Patient> waitingPatients = patientRepository.findAll().stream()
                .filter(Patient::isWaiting)
                .sorted(Comparator.comparing(Patient::getReceptionTime))
                .collect(Collectors.toList());
        model.addAttribute("patients", waitingPatients);
        return "doctor_waiting_list :: doctorWaitingFragment";
    }

    // 대기 리스트에서 환자 제거 (삭제)
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

    // 접수 통계
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

    // 환자 삭제
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientRepository.deleteById(id);
    }

    // 호출 대상 환자 정보 (nurse 모달용)
    @GetMapping("/call")
    @ResponseBody
    public Patient getCallPatient(@RequestParam Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
    }

    // 호출(확인) 시: 대기 상태는 유지하고, 호출된 상태(called)를 true로 업데이트
    @PostMapping("/call/confirm")
    @ResponseBody
    public Map<String, Object> confirmCall(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
            // waiting 상태 유지, 호출 상태 업데이트
            patient.setCalled(true);
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "호출 확인: 환자 호출이 완료되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "호출 확인에 실패하였습니다.");
        }
        return result;
    }

    // 진료 완료 시: 대기 상태를 false로 업데이트하여 대기 리스트에서 제거
    @PostMapping("/call/complete")
    @ResponseBody
    public Map<String, Object> completeCall(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
            patient.setWaiting(false);
            patient.setCalled(false);
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "진료 완료: 환자 진료 완료 처리되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "진료 완료 처리에 실패하였습니다.");
        }
        return result;
    }

    // 보류 시: 대기 상태 유지, receptionTime을 현재 시각으로 업데이트 (환자가 대기 리스트 하단으로 이동)
    @PostMapping("/call/hold")
    @ResponseBody
    public Map<String, Object> holdCall(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
            patient.setReceptionTime(LocalDateTime.now());
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "보류 처리: 환자가 대기 리스트 하단으로 이동되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "보류 처리에 실패하였습니다.");
        }
        return result;
    }
}
