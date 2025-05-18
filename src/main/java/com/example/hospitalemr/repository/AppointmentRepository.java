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
}
