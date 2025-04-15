package com.example.hospitalemr.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "diagnosis")
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_id")
    private Integer diagnosisId;

    @ManyToOne
    @JoinColumn(name = "visit_id", nullable = false)
    private MedicalVisit visit;

    @Column(name = "diagnosis_code")
    private String diagnosisCode;

    @Column(name = "diagnosis_name", columnDefinition = "TEXT")
    private String diagnosisName;

    @Column(name = "department", nullable = false)
    private String department;  // 기본값: "이비인후과" (DB default 또는 어플리케이션에서 설정)

    // 환자와 연동 : ManyToOne 관계 (DB의 patient_id 외래키)
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Getter / Setter
    public Integer getDiagnosisId() {
        return diagnosisId;
    }
    public void setDiagnosisId(Integer diagnosisId) {
        this.diagnosisId = diagnosisId;
    }

    public MedicalVisit getVisit() {
        return visit;
    }
    public void setVisit(MedicalVisit visit) {
        this.visit = visit;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }
    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }
    public void setDiagnosisName(String diagnosisName) {
        this.diagnosisName = diagnosisName;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public Patient getPatient() {
        return patient;
    }
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
