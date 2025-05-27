package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.MedicalVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    @Query(value = "SELECT DAYOFWEEK(visit_date) AS weekday, HOUR(visit_time) AS hour, COUNT(*) AS cnt"
            + " FROM medicalvisit"
            + " WHERE visit_date BETWEEN :start AND :end"
            + " GROUP BY DAYOFWEEK(visit_date), HOUR(visit_time)",
            nativeQuery = true)
    List<Object[]> findHourlyVisitCountsBetween(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}