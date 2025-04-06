package com.example.hospitalemr.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prescriptionId;

    // MedicalVisit와 연관관계 설정 (단방향 예시)
    @Column(name = "visit_id", nullable = false)
    private Long visitId;

    @Column(name = "drug_code")
    private String drugCode;

    @Column(name = "drug_name")
    private String drugName;

    @Column(name = "dosage")
    private String dosage;  // 용량

    private int days;       // 일수

    @Column(name = "insurance_yn")
    private boolean insuranceYn; // 급여 여부

    // getter / setter
    public Long getPrescriptionId() {
        return prescriptionId;
    }
    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }
    public Long getVisitId() {
        return visitId;
    }
    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }
    public String getDrugCode() {
        return drugCode;
    }
    public void setDrugCode(String drugCode) {
        this.drugCode = drugCode;
    }
    public String getDrugName() {
        return drugName;
    }
    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }
    public String getDosage() {
        return dosage;
    }
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    public int getDays() {
        return days;
    }
    public void setDays(int days) {
        this.days = days;
    }
    public boolean isInsuranceYn() {
        return insuranceYn;
    }
    public void setInsuranceYn(boolean insuranceYn) {
        this.insuranceYn = insuranceYn;
    }
}
