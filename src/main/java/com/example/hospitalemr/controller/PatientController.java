package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.*;
import com.example.hospitalemr.repository.*;
import com.example.hospitalemr.service.AlertSettingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientRepository patientRepository;
    private final MedicalVisitRepository medicalVisitRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final MedicalVisitController  medicalVisitController;
    private final AlertSettingService alertSettingService;

    public PatientController(PatientRepository patientRepository,
                             MedicalVisitRepository medicalVisitRepository,
                             PrescriptionRepository prescriptionRepository,
                             AppointmentRepository appointmentRepository,
                             DiagnosisRepository diagnosisRepository,
                             MedicalVisitController medicalVisitController, AlertSettingService alertSettingService) {
        this.patientRepository = patientRepository;
        this.medicalVisitRepository = medicalVisitRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.medicalVisitController = medicalVisitController;
        this.alertSettingService = alertSettingService;
    }

    // 환자 전체 조회
    @GetMapping
    public List<Patient> getAllPatient() {
        return patientRepository.findAll();
    }

    // 환자 등록
    @PostMapping
    @ResponseBody
    public Map<String, Object> createPatient(@ModelAttribute Patient patient) {
        Map<String, Object> result = new HashMap<>();
        try {
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "환자 등록에 성공하였습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "환자 등록에 실패하였습니다.");
        }
        return result;
    }

    // 환자 접수
    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> registerPatient(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));

            if(patient.isWaiting()) {
                result.put("success", false);
                result.put("message", "이미 접수된 환자입니다.");
                return result;
            }

            patient.setWaiting(true);
            patient.setReceptionTime(LocalDateTime.now());
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "환자 접수에 성공하였습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "환자 접수에 실패하였습니다.");
        }
        return result;
    }

    // 환자 검색
    @GetMapping("/search")
    public String searchPatients(@RequestParam String keyword, Model model) {
        List<Patient> allPatients = patientRepository.findAll();

        List<Patient> filtered = allPatients.stream()
                .filter(p -> {
                    if (p.getName() != null && p.getName().contains(keyword)) return true;
                    if (p.getDate_of_birth() != null && p.getDate_of_birth().toString().contains(keyword)) return true;
                    if (p.getPhone_number() != null && p.getPhone_number().contains(keyword)) return true;
                    return false;
                })
                .collect(Collectors.toList());

        model.addAttribute("patients", filtered);
        return "search_result :: resultFragment";
    }

    // 환자 정보 수정
    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @RequestBody Patient updateData) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + id));

        patient.setName(updateData.getName());
        patient.setPhone_number(updateData.getPhone_number());
        return patientRepository.save(patient);
    }

    // 대기 환자 리스트 (일반)
    @GetMapping("/waiting")
    public String getWaitingPatients(Model model) {
        List<Patient> waitingPatients = patientRepository.findAll().stream()
                .filter(Patient::isWaiting)
                .sorted(Comparator.comparing(Patient::getReceptionTime))
                .collect(Collectors.toList());
        model.addAttribute("patients", waitingPatients);
        return "waiting_list :: waitingFragment";
    }

    // 대기 환자 리스트 (의사용)
    @GetMapping("/doctor/waiting")
    public String getDoctorWaitingPatients(Model model) {
        List<Patient> waitingPatients = patientRepository.findAll().stream()
                .filter(Patient::isWaiting)
                .sorted(Comparator.comparing(Patient::getReceptionTime))
                .collect(Collectors.toList());
        model.addAttribute("patients", waitingPatients);
        return "doctor_waiting_list :: doctorWaitingFragment";
    }

    // 대기 리스트에서 환자 제거 (삭제)
    @PostMapping("/waiting/remove")
    @ResponseBody
    public Map<String, Object> removeFromWaiting(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
            patient.setWaiting(false);
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "대기 리스트에서 제거되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "대기 리스트에서 제거에 실패하였습니다.");
        }
        return result;
    }

    // 접수 통계
    @GetMapping("/stats")
    @ResponseBody
    public Map<String, Object> getPatientStats() {
        Map<String, Object> stats = new HashMap<>();
        long waitingCount = patientRepository.countByWaiting(true);
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        long collectionCount = patientRepository.countByWaitingFalseAndReceptionTimeBetween(start, end);
        stats.put("waitingCount", waitingCount);
        stats.put("collectionCount", collectionCount);
        return stats;
    }

    // 환자 삭제
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientRepository.deleteById(id);
    }

    // 호출 대상 환자 정보 (nurse 모달용)
    @GetMapping("/call")
    @ResponseBody
    public Patient getCallPatient(@RequestParam Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
    }

    // 호출(확인) 시: 대기 상태는 유지하고, 호출된 상태(called)를 true로 업데이트
    @PostMapping("/call/confirm")
    @ResponseBody
    public Map<String, Object> confirmCall(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
            // waiting 상태 유지, 호출 상태 업데이트
            patient.setCalled(true);
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "호출 확인: 환자 호출이 완료되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "호출 확인에 실패하였습니다.");
        }
        return result;
    }

    // 진료 완료 시: 대기 상태를 false로 업데이트하여 대기 리스트에서 제거
    @PostMapping("/call/complete")
    @ResponseBody
    public Map<String, Object> completeCall(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
            patient.setWaiting(false);
            patient.setCalled(false);
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "진료 완료: 환자 진료 완료 처리되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "진료 완료 처리에 실패하였습니다.");
        }
        return result;
    }

    // 보류 시: 대기 상태 유지, receptionTime을 현재 시각으로 업데이트 (환자가 대기 리스트 하단으로 이동)
    @PostMapping("/call/hold")
    @ResponseBody
    public Map<String, Object> holdCall(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
            patient.setReceptionTime(LocalDateTime.now());
            patientRepository.save(patient);
            result.put("success", true);
            result.put("message", "보류 처리: 환자가 대기 리스트 하단으로 이동되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "보류 처리에 실패하였습니다.");
        }
        return result;
    }

    @GetMapping("/receptionMemo")
    @ResponseBody
    public Map<String, Object> getReceptionMemo(@RequestParam Long patientId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MedicalVisit> visits = medicalVisitRepository.findByPatientIdOrderByVisitDateDescVisitTimeDesc(patientId);
            if (visits.isEmpty()) {
                result.put("success", false);
                result.put("message", "진료 기록이 없습니다.");
            } else {
                MedicalVisit latestVisit = visits.get(0);
                result.put("success", true);
                result.put("memo", latestVisit.getVisitReason());
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "오류: " + e.getMessage());
        }
        return result;
    }
    /**
     * 환자 홈 화면
     */
    @GetMapping("/home")
    public String showPatientHome(HttpSession session, Model model) {
        Long patientId = (Long) session.getAttribute("patientId");
        if (patientId == null) {
            return "redirect:/login";
        }

        // 본인 정보
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다. id=" + patientId));
        model.addAttribute("patient", patient);

        // 진료 내역
        List<MedicalVisit> visits = medicalVisitRepository
                .findByPatientIdOrderByVisitDateDescVisitTimeDesc(patientId);
        model.addAttribute("visits", visits);

        // 현재 진료중인 visitId 조회
        Map<String,Object> current = medicalVisitController.getCurrentConsultationPatient();
        Long currentVisitId = null;
        if (Boolean.TRUE.equals(current.get("success"))
                && current.get("visitId") instanceof Long) {
            currentVisitId = (Long) current.get("visitId");
        }
        model.addAttribute("currentVisitId", currentVisitId);

        // 처방 내역 (모든 진료기록에 연결된 처방)
        List<Prescription> prescriptions = new ArrayList<>();
        for (MedicalVisit v : visits) {
            prescriptions.addAll(prescriptionRepository.findByVisitId(v.getVisitId()));
        }
        model.addAttribute("prescriptions", prescriptions);

        // 내 예약 내역
        List<Appointment> appts = appointmentRepository.findAll().stream()
                .filter(a -> a.getPatient_id() == patientId.intValue())
                .sorted(Comparator.comparing(Appointment::getAppointment_date)
                        .thenComparing(Appointment::getAppointment_time))
                .collect(Collectors.toList());
        model.addAttribute("appointments", appts);

        // 신규 예약 폼 바인딩용 빈 객체
        model.addAttribute("newAppointment", new com.example.hospitalemr.domain.Appointment());

        AlertSetting setting = alertSettingService.loadOrCreate(patientId);
        model.addAttribute("alertSetting", setting);

        return "patient_home";  // src/main/resources/templates/patient_home.html
    }

    /**
     * 다가오는 예약 1시간 전 알림용 API
     */
    @GetMapping("/appointments/upcoming")
    @ResponseBody
    public List<Appointment> upcomingAppointments(HttpSession session) {
        Long patientId = (Long) session.getAttribute("patientId");
        LocalDateTime now = LocalDateTime.now();
        List<Appointment> all = appointmentRepository.findAll().stream()
                .filter(a -> a.getPatient_id() == patientId.intValue())
                .collect(Collectors.toList());

        return all.stream()
                .filter(a -> {
                    LocalDateTime appt = LocalDateTime.of(
                            a.getAppointment_date(), a.getAppointment_time());
                    return !appt.isBefore(now) && appt.isBefore(now.plusHours(1));
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/visit/{visitId}/detail")
    @ResponseBody
    public Map<String, Object> getVisitDetail(@PathVariable Long visitId,
                                              HttpSession session) throws AccessDeniedException {
        Long patientId = (Long) session.getAttribute("patientId");
        // 보안: 환자 본인 방문기록인지 검사
        MedicalVisit visit = medicalVisitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 방문입니다."));
        if (!visit.getPatientId().equals(patientId)) {
            throw new AccessDeniedException("본인 기록만 조회 가능합니다.");
        }
        List<Diagnosis> diagList = diagnosisRepository.findByVisitVisitId(visitId);
        List<Prescription> presList = prescriptionRepository.findByVisitId(visitId);
        Map<String, Object> map = new HashMap<>();
        map.put("visitDate", visit.getVisitDate().toString());
        map.put("visitTime", visit.getVisitTime().toString());
        map.put("diagnoses", diagList);
        map.put("prescriptions", presList);
        return map;
    }

}
