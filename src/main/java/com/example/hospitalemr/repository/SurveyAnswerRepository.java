package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {
    List<SurveyAnswer> findByPatientIdOrderBySurveyDateDesc(Integer patientId);

    List<SurveyAnswer> findByPatientIdAndSurveyDate(Integer patientId, LocalDateTime surveyDate);
}
