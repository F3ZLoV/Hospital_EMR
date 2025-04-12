package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.Appointment;
import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.repository.AppointmentRepository;
import com.example.hospitalemr.repository.PatientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository; // 추가된 부분

    public AppointmentController(AppointmentRepository appointmentRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    // 예약 등록 (등록 시 상태는 무조건 "예약"으로 처리)
    @PostMapping
    @ResponseBody
    public Map<String, Object> createAppointment(@ModelAttribute Appointment appointment) {
        Map<String, Object> result = new HashMap<>();
        try {
            appointment.setStatus("예약"); // 무조건 예약 상태로 설정
            appointmentRepository.save(appointment);
            result.put("success", true);
            result.put("message", "예약 등록에 성공하였습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "예약 등록에 실패하였습니다.");
        }
        return result;
    }

    // 예약 목록 조회 (예약 리스트 모달에 표시할 fragment 반환)
    @GetMapping("/list")
    public String getAppointmentList(Model model) {
        List<Map<String, Object>> appointmentList = appointmentRepository.findAll().stream().map(appointment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("appointmentId", appointment.getAppointment_id());
            map.put("patientId", appointment.getPatient_id());
            Patient patient = patientRepository.findById(Long.valueOf(appointment.getPatient_id())).orElse(null);
            if (patient != null) {
                map.put("patientName", patient.getName());
            } else {
                map.put("patientName", "Unknown");
            }
            map.put("appointmentDate", appointment.getAppointment_date());
            map.put("appointmentTime", appointment.getAppointment_time());
            map.put("status", appointment.getStatus());
            map.put("remarks", appointment.getRemarks());
            return map;
        }).collect(Collectors.toList());
        model.addAttribute("appointments", appointmentList);
        return "appointment_list :: listFragment";
    }

    // 예약 상태 업데이트 (완료, 취소로 변경)
    @PostMapping("/updateStatus")
    @ResponseBody
    public Map<String, Object> updateAppointmentStatus(@RequestParam int appointmentId,
                                                       @RequestParam String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다. id=" + appointmentId));
            appointment.setStatus(status);
            appointmentRepository.save(appointment);
            result.put("success", true);
            result.put("message", "예약 상태가 업데이트되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "예약 상태 업데이트에 실패하였습니다.");
        }
        return result;
    }

    @PostMapping("/updateMultipleStatus")
    @ResponseBody
    public Map<String, Object> updateMultipleStatus(@RequestBody List<Map<String, String>> updates) {
        Map<String, Object> result = new HashMap<>();
        try {
            for (Map<String, String> entry : updates) {
                int id = Integer.parseInt(entry.get("appointmentId"));
                String status = entry.get("status");
                Appointment appointment = appointmentRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("예약 없음: " + id));
                appointment.setStatus(status);
                appointmentRepository.save(appointment);
            }
            result.put("success", true);
            result.put("message", "모든 상태 업데이트 성공");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

}