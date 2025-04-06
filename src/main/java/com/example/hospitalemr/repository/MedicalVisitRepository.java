package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.MedicalVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalVisitRepository extends JpaRepository<MedicalVisit, Long> {
    List<MedicalVisit> findByPatientIdOrderByVisitDateDescVisitTimeDesc(Long patientId);

}