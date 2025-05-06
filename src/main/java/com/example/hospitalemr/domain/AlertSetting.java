package com.example.hospitalemr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "alert_setting")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class AlertSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK 컬럼
    @Column(name = "patient_id", nullable = false,
            insertable = false, updatable = false)
    private Long patientId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "hours_before", nullable = false)
    private int hoursBefore;

    @Column(name = "minutes_before", nullable = false)
    private int minutesBefore;

    // --- getters / setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) {
        this.patient = patient;
        this.patientId = (patient != null ? patient.getId() : null);
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getHoursBefore() { return hoursBefore; }
    public void setHoursBefore(int hoursBefore) { this.hoursBefore = hoursBefore; }

    public int getMinutesBefore() { return minutesBefore; }
    public void setMinutesBefore(int minutesBefore) { this.minutesBefore = minutesBefore; }
}
