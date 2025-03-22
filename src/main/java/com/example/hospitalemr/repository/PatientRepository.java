package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    long countByWaiting(boolean waiting);
    long countByWaitingFalseAndReceptionTimeBetween(LocalDateTime start, LocalDateTime end);

}
