package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MainController {
    @Autowired
    private PatientRepository patientRepository;

    public MainController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/doctor/main")
    public String doctorMain() { return "doctor"; }
    @GetMapping("/nurse/main")
    public String nurseMain() { return "nurse"; }
//    @GetMapping("/patient/home")
//    public String patientHome(HttpSession session, Model model) {
//        Long patientId = (Long) session.getAttribute("patientId");
//        if (patientId == null) {
//            return "redirect:/login?error=nosession";
//        }
//        Patient patient = patientRepository.findById(patientId)
//                .orElseThrow(() -> new RuntimeException("해당 환자 없음"));
//        model.addAttribute("patient", patient);
//        return "patient_home";
//    }
}
