package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
