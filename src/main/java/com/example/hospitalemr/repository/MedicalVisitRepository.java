package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.MedicalVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedicalVisitRepository extends JpaRepository<MedicalVisit, Long> {
    List<MedicalVisit> findByPatientIdOrderByVisitDateDescVisitTimeDesc(Long patientId);
    List<MedicalVisit> findAllByOrderByVisitDateDescVisitTimeDesc();
    List<MedicalVisit> findByPatientId(Long patientId);

    @Query(value = "SELECT visit_date, COUNT(*) AS cnt " +
            "FROM medicalvisit " +
            "GROUP BY visit_date " +
            "ORDER BY visit_date", nativeQuery = true)
    List<Object[]> findDailyVisitCountsNative();
}