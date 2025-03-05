package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private PatientRepository patientRepository;

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
    public Patient createPatient(@RequestBody Patient patient) {
        return patientRepository.save(patient);
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
        patient.setPhoneNumber(updateData.getPhoneNumber());
        return patientRepository.save(patient);
    }

    // 5) 환자 삭제
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientRepository.deleteById(id);
    }

}
