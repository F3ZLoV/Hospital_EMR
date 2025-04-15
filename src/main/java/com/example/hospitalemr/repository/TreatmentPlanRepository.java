package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Integer> {
}
