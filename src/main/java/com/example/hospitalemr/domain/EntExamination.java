package com.example.hospitalemr.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ent_examination")
public class EntExamination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examId;
    private Long visitId;
    private String examType;
    private String examSummary;
    @Lob
    private String examResultsText;
    private LocalDate examDate;
    @Lob
    private String examNotes;

    @ElementCollection
    @CollectionTable(
            name = "ent_examination_media",
            joinColumns = @JoinColumn(name = "exam_id")
    )
    @Column(name = "media_path")
    private List<String> examMediaList = new ArrayList<>();

    // ─── getters / setters ─────────────────────────────────────────────

    public Long getExamId() {
        return examId;
    }
    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getVisitId() {
        return visitId;
    }
    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }

    public String getExamType() {
        return examType;
    }
    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getExamSummary() {
        return examSummary;
    }
    public void setExamSummary(String examSummary) {
        this.examSummary = examSummary;
    }

    public String getExamResultsText() {
        return examResultsText;
    }
    public void setExamResultsText(String examResultsText) {
        this.examResultsText = examResultsText;
    }

    public LocalDate getExamDate() {
        return examDate;
    }
    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public String getExamNotes() {
        return examNotes;
    }
    public void setExamNotes(String examNotes) {
        this.examNotes = examNotes;
    }

    public List<String> getExamMediaList() {
        return examMediaList;
    }
    public void setExamMediaList(List<String> examMediaList) {
        this.examMediaList = examMediaList;
    }
}
