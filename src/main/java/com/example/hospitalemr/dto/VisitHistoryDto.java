package com.example.hospitalemr.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class VisitHistoryDto {
    private Long visitId;
    private LocalDate visitDate;
    private LocalTime visitTime;
    private String visitReason;      // 진료 메모 (예전 기록)
    private String clinicalNotes;    // 진료 기록
    private List<PrescriptionDto> prescriptions;

    public Long getVisitId() {
        return visitId;
    }
    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }
    public LocalDate getVisitDate() {
        return visitDate;
    }
    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }
    public LocalTime getVisitTime() {
        return visitTime;
    }
    public void setVisitTime(LocalTime visitTime) {
        this.visitTime = visitTime;
    }
    public String getVisitReason() {
        return visitReason;
    }
    public void setVisitReason(String visitReason) {
        this.visitReason = visitReason;
    }
    public String getClinicalNotes() {
        return clinicalNotes;
    }
    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }
    public List<PrescriptionDto> getPrescriptions() {
        return prescriptions;
    }
    public void setPrescriptions(List<PrescriptionDto> prescriptions) {
        this.prescriptions = prescriptions;
    }
}
