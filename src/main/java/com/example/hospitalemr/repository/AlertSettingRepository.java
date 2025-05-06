package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.AlertSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertSettingRepository extends JpaRepository<AlertSetting, Long> {
    // 특정 환자의 설정을 하나만 가져오기
    Optional<AlertSetting> findByPatientId(Long patientId);
}
