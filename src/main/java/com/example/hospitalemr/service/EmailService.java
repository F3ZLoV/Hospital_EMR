package com.example.hospitalemr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendReservationEmail(String to, String patientName, String date, String time) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[이비인후과] 예약이 완료되었습니다.");
        message.setText(String.format("%s님,\n\n%s %s에 진료 예약이 완료되었습니다.\n\n감사합니다.", patientName, date, time));
        mailSender.send(message);
    }
}
