package com.example.hospitalemr.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    private String name;

    @Column(name = "date_of_birth")
    private LocalDate date_of_birth;

    private String gender;

    @Column(name = "phone_number")
    private String phone_number;

    private String email;
    private String address;

    private boolean waiting = false; // 환자 대기 여부 (true = 대기중)
    private boolean called = false; // 호출 여부

    @Column(name = "reception_time", columnDefinition = "TIMESTAMP DEFAULT NULL")
    private LocalDateTime receptionTime;


    public Patient() {}

    public boolean isCalled() {
        return called;
    }

    public void setCalled(boolean called) {
        this.called = called;
    }

    public Patient(String name, String phone_number) {
        this.name = name;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.phone_number = phone_number;
        this.email = email;
        this.address = address;
        this.waiting = true;
    }
    @PrePersist
    public void prePersist() {
        if (this.receptionTime == null) {
            this.receptionTime = LocalDateTime.now();
        }
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(LocalDate dateOfBirth) {
        this.date_of_birth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phoneNumber) {
        this.phone_number = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isWaiting() {
        return waiting;
    }
    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public LocalDateTime getReceptionTime() {
        return receptionTime;
    }

    public void setReceptionTime(LocalDateTime receptionTime) {
        this.receptionTime = receptionTime;
    }
}