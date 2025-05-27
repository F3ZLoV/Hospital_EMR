package com.example.hospitalemr.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class SurveyAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="question_id")
    private SurveyQuestion question;

    @Column(nullable=false)
    private Integer patientId; // patient 테이블의 patient_id (INT)

    @Column(nullable=false)
    private String answerText;

    @Column(nullable=false)
    private LocalDateTime surveyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SurveyQuestion getQuestion() {
        return question;
    }

    public void setQuestion(SurveyQuestion question) {
        this.question = question;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public LocalDateTime getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(LocalDateTime surveyDate) {
        this.surveyDate = surveyDate;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
