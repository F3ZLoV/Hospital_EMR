package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.Diagnosis;
import com.example.hospitalemr.domain.MedicalVisit;
import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.dto.DiagnosisDTO;
import com.example.hospitalemr.repository.DiagnosisRepository;
import com.example.hospitalemr.repository.MedicalVisitRepository;
import com.example.hospitalemr.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/diagnosis")
public class DiagnosisController {

    private final DiagnosisRepository diagnosisRepository;
    private final PatientRepository patientRepository;
    private final MedicalVisitRepository medicalVisitRepository;

    public DiagnosisController(DiagnosisRepository diagnosisRepository,
                               PatientRepository patientRepository,
                               MedicalVisitRepository medicalVisitRepository) {
        this.diagnosisRepository = diagnosisRepository;
        this.patientRepository = patientRepository;
        this.medicalVisitRepository = medicalVisitRepository;
    }

    // 다중 진단 행 저장
    @PostMapping("/saveAll")
    public Map<String, Object> saveAllDiagnoses(@RequestBody List<DiagnosisDTO> diagnosisDTOList) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Diagnosis> diagnosisList = new ArrayList<>();
            for (DiagnosisDTO dto : diagnosisDTOList) {
                Diagnosis diagnosis = new Diagnosis();
                Patient patient = patientRepository.findById(dto.getPatientId())
                        .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + dto.getPatientId()));
                MedicalVisit visit = medicalVisitRepository.findById(dto.getVisitId())
                        .orElseThrow(() -> new RuntimeException("진료 기록을 찾을 수 없습니다. id=" + dto.getVisitId()));
                diagnosis.setPatient(patient);
                diagnosis.setVisit(visit);
                diagnosis.setDiagnosisCode(dto.getDiagnosisCode());
                diagnosis.setDiagnosisName(dto.getDiagnosisName());
                diagnosis.setDepartment(dto.getDepartment());
                diagnosisList.add(diagnosis);
            }
            diagnosisRepository.saveAll(diagnosisList);
            result.put("success", true);
            result.put("message", "진단 정보가 성공적으로 저장되었습니다.");
            result.put("diagnoses", diagnosisList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "진단 정보 저장 실패: " + e.getMessage());
        }
        return result;
    }
}
