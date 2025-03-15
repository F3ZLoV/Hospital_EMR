package com.example.hospitalemr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // login.html
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password) {

        // 더미 로직: 의사 계정
        if ("doctor".equals(username) && "doc123".equals(password)) {
            return "redirect:/doctor/main";
        }
        // 더미 로직: 간호사 계정
        else if ("nurse".equals(username) && "nurse123".equals(password)) {
            return "redirect:/nurse/main";
        }
        // 실패 시 → 로그인 페이지로 에러 파라미터 전달
        else {
            return "redirect:/login?error=true";
        }
    }

    // 의사 메인 페이지
    @GetMapping("/doctor/main")
    public String showDoctorMain() {
        return "doctor"; // doctor.html
    }

    // 간호사 메인 페이지
    @GetMapping("/nurse/main")
    public String showNurseMain() {
        return "nurse"; // nurse.html
    }
}
