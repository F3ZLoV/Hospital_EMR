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
            LocalDate newDate = appointment.getAppointment_date();
            LocalTime newTime = appointment.getAppointment_time();

            // 같은 날짜의 예약 모두 불러옴
            List<Appointment> sameDateAppointments = appointmentRepository.findAll().stream()
                    .filter(appt -> appt.getAppointment_date().equals(newDate))
                    .collect(Collectors.toList());

            boolean conflict = sameDateAppointments.stream().anyMatch(existing -> {
                long diff = Math.abs(java.time.Duration.between(existing.getAppointment_time(), newTime).toMinutes());
                return diff < 5;
            });

            if (conflict) {
                result.put("success", false);
                result.put("message", "예약 시간이 기존 예약과 5분 이내로 겹칩니다.");
                return result;
            }

            // 중복 없을 경우 저장
            appointment.setStatus("예약");
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
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        List<Map<String, Object>> appointmentList = appointmentRepository.findAll().stream()
                .filter(appointment -> {
                    LocalDate apptDate = appointment.getAppointment_date();
                    LocalTime apptTime = appointment.getAppointment_time();
                    if (apptDate.isBefore(currentDate)) {
                        return false;
                    }
                    if (apptDate.equals(currentDate) && apptTime.isBefore(currentTime)) {
                        return false;
                    }
                    return true;
                })
                // 예약 날짜와 시간이 빠른 순서로 정렬 (즉, 현재와 제일 가까운 예약이 위쪽에 오도록)
                .sorted((a1, a2) -> {
                    int cmp = a1.getAppointment_date().compareTo(a2.getAppointment_date());
                    if (cmp == 0) {
                        cmp = a1.getAppointment_time().compareTo(a2.getAppointment_time());
                    }
                    return cmp;
                })
                .map(appointment -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("appointmentId", appointment.getAppointment_id());
                    map.put("patientId", appointment.getPatient_id());
                    Patient patient = patientRepository.findById(Long.valueOf(appointment.getPatient_id())).orElse(null);
                    map.put("patientName", patient != null ? patient.getName() : "Unknown");
                    map.put("appointmentDate", appointment.getAppointment_date());
                    map.put("appointmentTime", appointment.getAppointment_time());
                    map.put("status", appointment.getStatus());  // 예: "예약", "완료", "취소"
                    map.put("remarks", appointment.getRemarks());
                    return map;
                })
                .collect(Collectors.toList());

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

}