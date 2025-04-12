package com.example.hospitalemr.domain;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_id")
    private Long prescriptionId;

    @Column(name = "visit_id", nullable = false)
    private Long visitId;

    @Column(name = "drug_code")
    private String drugCode;

    @Column(name = "drug_name")
    private String drugName;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "days")
    private Integer days;

    @Column(name = "insurance_yn")
    private Boolean insuranceYn;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;

    // 기본 생성자
    public Prescription() {}

    // Getters and Setters

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

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Boolean getInsuranceYn() {
        return insuranceYn;
    }

    public void setInsuranceYn(Boolean insuranceYn) {
        this.insuranceYn = insuranceYn;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
