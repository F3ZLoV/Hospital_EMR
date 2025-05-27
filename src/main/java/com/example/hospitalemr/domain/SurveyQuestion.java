package com.example.hospitalemr.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SurveyQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String questionText;

    @Column(nullable=false)
    private String questionType; // "TEXT", "CHOICE", "OX"

    private String choiceOptions; // 콤마로 구분 (ex: "콧물,코막힘,재채기")

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getChoiceOptions() {
        return choiceOptions;
    }

    public void setChoiceOptions(String choiceOptions) {
        this.choiceOptions = choiceOptions;
    }
}
