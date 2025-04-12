package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.MedicalVisit;
import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.domain.Prescription;
import com.example.hospitalemr.repository.MedicalVisitRepository;
import com.example.hospitalemr.repository.PatientRepository;
import com.example.hospitalemr.repository.PrescriptionRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/medicalvisit")
public class MedicalVisitController {
    private final MedicalVisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;

    public MedicalVisitController(MedicalVisitRepository visitRepository,
                                  PatientRepository patientRepository,
                                  PrescriptionRepository prescriptionRepository) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
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

            visitRepository.save(medicalVisit);

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
            @RequestParam String memo,       // 진료 메모 (예: visitReason)
            @RequestParam String record) {   // 진료 기록 (예: clinicalNotes)
        Map<String, Object> result = new HashMap<>();
        try {
            MedicalVisit visit = visitRepository.findById(visitId)
                    .orElseThrow(() -> new RuntimeException("진료 기록을 찾을 수 없습니다. id=" + visitId));
            visit.setVisitReason(memo);
            visit.setClinicalNotes(record);
            visitRepository.save(visit);
            result.put("success", true);
            result.put("message", "진료 기록이 업데이트되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "진료 기록 업데이트에 실패하였습니다.");
        }
        return result;
    }

    // 현재 진료중인 환자 정보를 반환하는 엔드포인트 (기존 코드 참고)
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
                // 진료 기록에 필요한 visitId와 patientId를 함께 반환 (visitId는 최신 진료 기록의 id)
                Optional<MedicalVisit> latestVisitOpt = visitRepository.findByPatientIdOrderByVisitDateDescVisitTimeDesc(current.getId()).stream().findFirst();
                if (latestVisitOpt.isPresent()) {
                    result.put("visitId", latestVisitOpt.get().getVisitId());
                } else {
                    result.put("visitId", null);
                }
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

    // 새 엔드포인트: 역대 진료 기록 조회 (해당 환자의 모든 진료 기록과 처방 내역 포함)
    @GetMapping("/history")
    @ResponseBody
    public List<Map<String, Object>> getVisitHistory(@RequestParam Long patientId) {
        List<MedicalVisit> visits = visitRepository.findByPatientIdOrderByVisitDateDescVisitTimeDesc(patientId);
        List<Map<String, Object>> history = new ArrayList<>();
        for (MedicalVisit visit : visits) {
            Map<String, Object> map = new HashMap<>();
            map.put("visitId", visit.getVisitId());
            map.put("visitDate", visit.getVisitDate());
            map.put("visitTime", visit.getVisitTime());
            map.put("visitReason", visit.getVisitReason());
            map.put("clinicalNotes", visit.getClinicalNotes());
            // 처방 내역 조회
            List<Prescription> prescriptions = prescriptionRepository.findByVisitId(visit.getVisitId());
            map.put("prescriptions", prescriptions);
            history.add(map);
        }
        return history;
    }

    // 환자 상담 메모 업데이트 (Patient 테이블 업데이트)
    @PostMapping("/updatePatientMemo")
    @ResponseBody
    public Map<String, Object> updatePatientMemo(@RequestParam Long patientId, @RequestParam String memo) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
            // 예를 들어, Patient 엔티티에 consultationMemo 필드가 있다고 가정
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

    // 진료 기록(Clinical Notes) 업데이트 (MedicalVisit 테이블 업데이트)
    @PostMapping("/updateClinicalNotes")
    @ResponseBody
    public Map<String, Object> updateClinicalNotes(@RequestParam Long patientId, @RequestParam String record) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MedicalVisit> visits = visitRepository.findByPatientIdOrderByVisitDateDescVisitTimeDesc(patientId);
            if (visits.isEmpty()) {
                throw new RuntimeException("진료 기록이 없습니다.");
            }
            MedicalVisit latestVisit = visits.get(0);
            latestVisit.setClinicalNotes(record);
            visitRepository.save(latestVisit);
            result.put("success", true);
            result.put("message", "진료 기록이 업데이트되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "업데이트 실패: " + e.getMessage());
        }
        return result;
    }
}
