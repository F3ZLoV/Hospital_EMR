package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    long countByWaiting(boolean waiting);
    long countByWaitingFalseAndReceptionTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query(value =
            "SELECT CONCAT(FLOOR(YEAR(CURDATE()) - YEAR(date_of_birth) / 10) * 10, '-', FLOOR(YEAR(CURDATE()) - YEAR(date_of_birth) / 10) * 10 + 9) AS age_group, " +
                    "COUNT(*) AS cnt " +
                    "FROM patient " +
                    "GROUP BY age_group " +
                    "ORDER BY age_group", nativeQuery = true)
    List<Object[]> findAgeGroupDistributionNative();
}
