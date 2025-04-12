package com.example.hospitalemr.dto;

public class PrescriptionDto {
    private String drugCode;
    private String drugName;
    private String dosage;
    private Integer days;
    private boolean insuranceYn;

    public PrescriptionDto(String drugCode, String drugName, String dosage, Integer days, boolean insuranceYn) {
        this.drugCode = drugCode;
        this.drugName = drugName;
        this.dosage = dosage;
        this.days = days;
        this.insuranceYn = insuranceYn;
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
    public boolean isInsuranceYn() {
        return insuranceYn;
    }
    public void setInsuranceYn(boolean insuranceYn) {
        this.insuranceYn = insuranceYn;
    }
}
