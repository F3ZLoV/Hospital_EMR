package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.EntExamination;
import com.example.hospitalemr.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntExaminationRepository extends JpaRepository<EntExamination, Long> {
    List<EntExamination> findByVisitId(Long visitId);
}
