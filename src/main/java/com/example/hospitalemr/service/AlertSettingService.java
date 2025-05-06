package com.example.hospitalemr.service;

import com.example.hospitalemr.domain.AlertSetting;
import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.repository.AlertSettingRepository;
import com.example.hospitalemr.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlertSettingService {

    private final AlertSettingRepository repo;
    private final PatientRepository patientRepo;

    public AlertSettingService(AlertSettingRepository repo,
                               PatientRepository patientRepo) {
        this.repo = repo;
        this.patientRepo = patientRepo;
    }

    public AlertSetting loadOrCreate(Long patientId) {
        return repo.findByPatientId(patientId)
                .orElseGet(() -> {
                    Patient p = patientRepo.findById(patientId)
                            .orElseThrow(() -> new EntityNotFoundException("환자를 찾을 수 없습니다: " + patientId));
                    AlertSetting s = new AlertSetting();
                    s.setPatient(p);
                    s.setEnabled(false);
                    s.setHoursBefore(1);
                    s.setMinutesBefore(0);
                    return repo.save(s);
                });
    }

    public AlertSetting save(AlertSetting setting) {
        return repo.save(setting);
    }
}