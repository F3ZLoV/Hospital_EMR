package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.MedicalVisit;
import com.example.hospitalemr.repository.MedicalVisitRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/nurse")
public class NurseController {

    private final MedicalVisitRepository visitRepo;

    public NurseController(MedicalVisitRepository visitRepo) {
        this.visitRepo = visitRepo;
    }

    // 간호사 페이지 보여주기
    @GetMapping("/page")
    public String showNursePage(Model model) {
        return "nurse";
    }

    // 최근 MedicalVisit 목록 (JSON)
    @GetMapping("/prescriptions")
    @ResponseBody
    public List<MedicalVisit> recentVisits() {
        return visitRepo.findAllByOrderByVisitDateDescVisitTimeDesc();
    }

}
