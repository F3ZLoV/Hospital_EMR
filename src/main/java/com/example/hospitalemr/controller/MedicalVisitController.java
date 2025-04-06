package com.example.hospitalemr.controller;


import com.example.hospitalemr.domain.MedicalVisit;
import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.repository.MedicalVisitRepository;
import com.example.hospitalemr.repository.PatientRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/medicalvisit")
public class MedicalVisitController {
    private final MedicalVisitRepository visitRepository;
    private final MedicalVisitRepository medicalVisitRepository;
    private final PatientRepository patientRepository;

    public MedicalVisitController(MedicalVisitRepository visitRepository, MedicalVisitRepository medicalVisitRepository, PatientRepository patientRepository) {
        this.visitRepository = visitRepository;
        this.medicalVisitRepository = medicalVisitRepository;
        this.patientRepository = patientRepository;
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

    @PostMapping("/updateNotes")
    @ResponseBody
    public Map<String, Object> updateMedicalVisitNotes(
            @RequestParam Long visitId,
            @RequestParam String memo,       // 좌측 카드의 진료 메모 (예: visitReason)
            @RequestParam String record) {   // 우측 카드의 진료 기록 (예: clinicalNotes)
        Map<String, Object> result = new HashMap<>();
        try {
            MedicalVisit visit = medicalVisitRepository.findById(visitId)
                    .orElseThrow(() -> new RuntimeException("진료 기록을 찾을 수 없습니다. id=" + visitId));
            // 업데이트: 여기서는 visitReason에 진료 메모, clinicalNotes에 기록 내용을 저장한다고 가정
            visit.setVisitReason(memo);
            visit.setClinicalNotes(record);
            medicalVisitRepository.save(visit);
            result.put("success", true);
            result.put("message", "진료 기록이 업데이트되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "진료 기록 업데이트에 실패하였습니다.");
        }
        return result;
    }
    @GetMapping("/currentPatient")
    @ResponseBody
    public Map<String, Object> getCurrentConsultationPatient() {
        Map<String, Object> result = new HashMap<>();
        try {
            // waiting = true, called = true 인 환자 중 receptionTime이 가장 최신인 환자 선택
            List<Patient> patients = patientRepository.findAll().stream()
                    .filter(p -> p.isWaiting() && p.isCalled())
                    .sorted((p1, p2) -> p2.getReceptionTime().compareTo(p1.getReceptionTime()))
                    .collect(Collectors.toList());
            if (patients.isEmpty()) {
                result.put("success", false);
                result.put("message", "현재 진료중인 환자가 없습니다.");
            } else {
                Patient current = patients.get(0);
                result.put("success", true);
                result.put("patientId", current.getId());
                result.put("patientName", current.getName());
                if (current.getDate_of_birth() != null) {
                    int age = LocalDate.now().getYear() - current.getDate_of_birth().getYear();
                    result.put("patientAge", age);
                } else {
                    result.put("patientAge", "N/A");
                }
                result.put("patientGender", current.getGender());
                result.put("patientPhone", current.getPhone_number());
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "오류: " + e.getMessage());
        }
        return result;
    }

    // [새 엔드포인트] 환자 상담 메모 업데이트 (Patient 테이블 업데이트)
    @PostMapping("/updatePatientMemo")
    @ResponseBody
    public Map<String, Object> updatePatientMemo(@RequestParam Long patientId, @RequestParam String memo) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
            patient.setConsultationMemo(memo);
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "환자 상담 메모가 업데이트되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "업데이트 실패: " + e.getMessage());
        }
        return result;
    }

    // [새 엔드포인트] 진료 기록(Clinical Notes) 업데이트 (MedicalVisit 테이블 업데이트)
    @PostMapping("/updateClinicalNotes")
    @ResponseBody
    public Map<String, Object> updateClinicalNotes(@RequestParam Long patientId, @RequestParam String record) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 해당 환자의 가장 최근 진료 기록을 조회
            List<MedicalVisit> visits = medicalVisitRepository.findByPatientIdOrderByVisitDateDescVisitTimeDesc(patientId);
            if (visits.isEmpty()) {
                throw new RuntimeException("진료 기록이 없습니다.");
            }
            MedicalVisit latestVisit = visits.get(0);
            latestVisit.setClinicalNotes(record);
            medicalVisitRepository.save(latestVisit);
            result.put("success", true);
            result.put("message", "진료 기록이 업데이트되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "업데이트 실패: " + e.getMessage());
        }
        return result;
    }
}
