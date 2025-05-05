package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.MedicalVisit;
import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.domain.Diagnosis;
import com.example.hospitalemr.domain.EntExamination;
import com.example.hospitalemr.domain.Prescription;
import com.example.hospitalemr.repository.MedicalVisitRepository;
import com.example.hospitalemr.repository.PatientRepository;
import com.example.hospitalemr.repository.DiagnosisRepository;
import com.example.hospitalemr.repository.EntExaminationRepository;
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
    private final PatientRepository     patientRepository;
    private final DiagnosisRepository   diagnosisRepository;
    private final EntExaminationRepository examinationRepository;
    private final PrescriptionRepository   prescriptionRepository;

    public MedicalVisitController(MedicalVisitRepository visitRepository,
                                  PatientRepository patientRepository,
                                  DiagnosisRepository diagnosisRepository,
                                  EntExaminationRepository examinationRepository,
                                  PrescriptionRepository prescriptionRepository) {
        this.visitRepository       = visitRepository;
        this.patientRepository     = patientRepository;
        this.diagnosisRepository   = diagnosisRepository;
        this.examinationRepository = examinationRepository;
        this.prescriptionRepository= prescriptionRepository;
    }

    /**
     * 1) 접수(create): visitReason(접수 메모)만 저장
     */
    @PostMapping("/create")
    @ResponseBody
    public Map<String,Object> createMedicalVisit(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate visitDate,
            @RequestParam @DateTimeFormat(pattern = "HH:mm")
            LocalTime visitTime,
            @RequestParam String visitReason,
            @RequestParam String visitType) {

        Map<String,Object> result = new HashMap<>();
        try {
            MedicalVisit mv = new MedicalVisit();
            mv.setPatientId(patientId);
            mv.setDoctorId(doctorId);
            mv.setVisitDate(visitDate);
            mv.setVisitTime(visitTime);
            mv.setVisitReason(visitReason);  // **접수 메모**
            mv.setVisitType(visitType);
            // clinicalMemo는 아직 비어있음
            visitRepository.save(mv);

            result.put("success", true);
            result.put("visitId", mv.getVisitId());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "진료 접수 등록에 실패하였습니다.");
        }
        return result;
    }

    /**
     * 2) 호출(call): 버튼 클릭 시 명시적으로 호출 플래그를 세우고
     *                새 MedicalVisit 레코드를 만듭니다.
     */
    @PostMapping("/call")
    @ResponseBody
    public Map<String,Object> callPatient(
            @RequestParam Long patientId,
            @RequestParam Long doctorId) {

        Map<String,Object> result = new HashMap<>();
        try {
            // ① 환자 호출 플래그 업데이트
            Patient p = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다."));
            p.setCalled(true);
            patientRepository.save(p);

            // ② 진료 레코드 생성
            MedicalVisit mv = new MedicalVisit();
            mv.setPatientId(patientId);
            mv.setDoctorId(doctorId);
            mv.setVisitDate(LocalDate.now());
            mv.setVisitTime(LocalTime.now());
            mv.setVisitReason("");   // 진료 메모는 별도 처리
            mv.setVisitType("진료");
            visitRepository.save(mv);

            result.put("success", true);
            result.put("visitId", mv.getVisitId());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "호출 처리에 실패하였습니다.");
        }
        return result;
    }

    /**
     * 3) currentPatient: 이미 만들어진 최신 MedicalVisit만 조회
     */
    @GetMapping("/currentPatient")
    @ResponseBody
    public Map<String,Object> getCurrentConsultationPatient() {
        Map<String,Object> result = new HashMap<>();
        try {
            // (1) waiting && called 인 환자 중 가장 최근 접수 시간 순
            List<Patient> patients = patientRepository.findAll().stream()
                    .filter(p -> p.isWaiting() && p.isCalled())
                    .sorted(Comparator.comparing(Patient::getReceptionTime).reversed())
                    .collect(Collectors.toList());

            if (patients.isEmpty()) {
                result.put("success", false);
                result.put("message", "현재 진료중인 환자가 없습니다.");
                return result;
            }

            Patient current = patients.get(0);
            result.put("success", true);
            result.put("patientId",   current.getId());
            result.put("patientName", current.getName());
            if (current.getDate_of_birth() != null) {
                int age = LocalDate.now().getYear() - current.getDate_of_birth().getYear();
                result.put("patientAge", age);
            } else {
                result.put("patientAge", "N/A");
            }
            result.put("patientGender", current.getGender());
            result.put("patientPhone",  current.getPhone_number());

            // (2) 기존에 만들어진 가장 최신 MedicalVisit 레코드만 꺼내서
            List<MedicalVisit> visits = visitRepository
                    .findByPatientIdOrderByVisitDateDescVisitTimeDesc(current.getId());
            if (visits.isEmpty()) {
                result.put("visitId", null);
            } else {
                MedicalVisit latest = visits.get(0);
                result.put("visitId",        latest.getVisitId());
                result.put("visitReason",    latest.getVisitReason());
                result.put("clinicalNotes",  latest.getClinicalNotes());
                result.put("clinicalMemo",   latest.getClinicalMemo());
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "오류: " + e.getMessage());
        }
        return result;
    }

    /**
     * 4) 진료 메모(clinicalMemo)만 업데이트
     *    (visitReason, clinicalNotes 절대 건드리지 않습니다)
     */
    @PostMapping("/updateClinicalMemo")
    @ResponseBody
    public Map<String,Object> updateClinicalMemo(
            @RequestParam Long visitId,
            @RequestParam String clinicalMemo) {

        Map<String,Object> result = new HashMap<>();
        try {
            MedicalVisit v = visitRepository.findById(visitId)
                    .orElseThrow(() -> new RuntimeException("진료 레코드를 찾을 수 없습니다."));
            v.setClinicalMemo(clinicalMemo);
            visitRepository.save(v);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "진료 메모 업데이트에 실패하였습니다.");
        }
        return result;
    }

    /**
     * 5) 진료 기록(Clinical Notes) 업데이트
     */
    @PostMapping("/updateClinicalNotes")
    @ResponseBody
    public Map<String,Object> updateClinicalNotes(
            @RequestParam Long visitId,
            @RequestParam("record") String clinicalNotes) {

        Map<String,Object> result = new HashMap<>();
        try {
            MedicalVisit v = visitRepository.findById(visitId)
                    .orElseThrow(() -> new RuntimeException("진료 레코드를 찾을 수 없습니다."));
            v.setClinicalNotes(clinicalNotes);
            visitRepository.save(v);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "진료 기록 업데이트에 실패하였습니다.");
        }
        return result;
    }

    /**
     * 6) 역대 진료 기록 조회
     */
    @GetMapping("/history")
    @ResponseBody
    public List<Map<String,Object>> getVisitHistory(@RequestParam Long patientId) {
        List<MedicalVisit> visits = visitRepository
                .findByPatientIdOrderByVisitDateDescVisitTimeDesc(patientId);
        List<Map<String,Object>> history = new ArrayList<>();

        for (MedicalVisit v : visits) {
            Map<String,Object> m = new HashMap<>();
            m.put("visitId",       v.getVisitId());
            m.put("visitDate",     v.getVisitDate());
            m.put("visitTime",     v.getVisitTime());
            m.put("visitReason",   v.getVisitReason());
            m.put("clinicalNotes", v.getClinicalNotes());
            m.put("clinicalMemo",  v.getClinicalMemo());
            m.put("prescriptions", prescriptionRepository.findByVisitId(v.getVisitId()));
            m.put("diagnoses",      diagnosisRepository.findByVisitVisitId(v.getVisitId()));
            m.put("examinations",   examinationRepository.findByVisitId(v.getVisitId()));
            history.add(m);
        }
        return history;
    }

    /**
     * 7) 대기열에서 취소할 때: 해당 환자의 최신 MedicalVisit 레코드를 삭제
     */
    @PostMapping("/cancel")
    @ResponseBody
    public Map<String,Object> cancelVisit(@RequestParam Long patientId) {
        Map<String,Object> result = new HashMap<>();
        try {
            List<MedicalVisit> visits = visitRepository
                    .findByPatientIdOrderByVisitDateDescVisitTimeDesc(patientId);
            if (!visits.isEmpty()) {
                visitRepository.delete(visits.get(0));
            }
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "취소 처리에 실패하였습니다.");
        }
        return result;
    }

}
