package com.example.hospitalemr.service;

import com.example.hospitalemr.domain.Appointment;
import com.example.hospitalemr.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService; // 추가

    public AppointmentService(AppointmentRepository appointmentRepository, EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    public boolean isAppointmentTimeAvailable(LocalDate date, LocalTime time) {
        List<Appointment> appointments = appointmentRepository.findByDate(date);
        for (Appointment appt : appointments) {
            long minutesDiff = Math.abs(ChronoUnit.MINUTES.between(appt.getAppointment_time(), time));
            if (minutesDiff < 5) {
                return false;
            }
        }
        return true;
    }

    public void registerAppointment(Appointment appointment, String patientName, String email) {
        if (!isAppointmentTimeAvailable(appointment.getAppointment_date(), appointment.getAppointment_time())) {
            throw new IllegalArgumentException("예약 시간이 기존 예약과 5분 이내로 겹칩니다.");
        }
        appointmentRepository.save(appointment);
        // 이메일 전송 (이름, 이메일, 날짜, 시간 전달)
        emailService.sendReservationEmail(
                email,
                patientName,
                appointment.getAppointment_date().toString(),
                appointment.getAppointment_time().toString()
        );
    }
}
