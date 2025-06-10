package com.example.hospitalemr.service;

import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // 단순 정보 수정
    public void updateContactInfo(Long patientId, String phone_number, String address, String email) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("환자 정보를 찾을 수 없습니다."));
        patient.setPhone_number(phone_number);
        patient.setAddress(address);
        patient.setEmail(email);
        patientRepository.save(patient);
    }

    // 단순 조회 (프로필 화면용)
    public Patient findById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("환자 정보를 찾을 수 없습니다."));
    }
}
