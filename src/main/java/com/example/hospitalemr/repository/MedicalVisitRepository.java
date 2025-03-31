package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.MedicalVisit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalVisitRepository extends JpaRepository<MedicalVisit, Long> {
}