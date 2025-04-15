package com.example.hospitalemr.dto;

public class DiagnosisDTO {
    private Long visitId;
    private Long patientId;
    private String diagnosisCode;
    private String diagnosisName;
    private String department;

    public Long getVisitId() { return visitId; }
    public void setVisitId(Long visitId) { this.visitId = visitId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getDiagnosisCode() { return diagnosisCode; }
    public void setDiagnosisCode(String diagnosisCode) { this.diagnosisCode = diagnosisCode; }
    public String getDiagnosisName() { return diagnosisName; }
    public void setDiagnosisName(String diagnosisName) { this.diagnosisName = diagnosisName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
