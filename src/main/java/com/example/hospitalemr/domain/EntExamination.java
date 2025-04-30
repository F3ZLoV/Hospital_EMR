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

    /** 검사 종류 */
    private String examType;

    /** 검사 요약 – 추가된 필드 */
    private String examSummary;

    /** 검사 결과 상세 텍스트 */
    @Lob
    private String examResultsText;

    /** 검사 일자 */
    private LocalDate examDate;

    /** 추가 메모 */
    @Lob
    private String examNotes;

    /**
     * 다중 이미지 경로 저장.
     * ent_examination_media 테이블에 exam_id 외래키로 media_path 로 저장됩니다.
     */
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
