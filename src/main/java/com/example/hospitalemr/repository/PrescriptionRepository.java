package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByVisitId(Long visitId);
    List<Prescription> findByVisitIdIn(List<Long> visitIds);
    @Query(value = "SELECT drug_name, COUNT(*) AS count FROM prescription GROUP BY drug_name ORDER BY count DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopDrugsNative();
}
