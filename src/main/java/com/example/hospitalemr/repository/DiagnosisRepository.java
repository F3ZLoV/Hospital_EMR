package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Integer> {
    // 'Patient' 엔티티의 기본키 필드가 'id'라고 가정하여 작성
    List<Diagnosis> findByPatient_IdOrderByDiagnosisIdDesc(Integer patientId);
}
