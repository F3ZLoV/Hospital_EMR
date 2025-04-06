package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    // 필요시 커스텀 쿼리 메서드 추가
}
