package com.example.hospitalemr.repository;

import com.example.hospitalemr.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    @Query("SELECT a FROM Appointment a WHERE a.appointment_date = :date")
    List<Appointment> findByDate(@Param("date") LocalDate date);
    @Query("SELECT a FROM Appointment a WHERE a.patient_id = :patientId")
    List<Appointment> findByPatientId(@Param("patientId") int patientId);
    // 예약 날짜로만 리스트 조회
    @Query("SELECT a FROM Appointment a WHERE a.appointment_date = :date")
    List<Appointment> findByAppointment_date(@Param("date") LocalDate appointment_date);



}
