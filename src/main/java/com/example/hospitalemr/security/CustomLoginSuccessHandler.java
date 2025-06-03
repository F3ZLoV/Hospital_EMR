package com.example.hospitalemr.security;

import com.example.hospitalemr.auth.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String role = userDetails.getRole().name();
        Long linkedId = userDetails.getLinkedId();

        switch (role) {
            case "PATIENT" -> {
                // 세션에 patientId 저장
                request.getSession().setAttribute("patientId", linkedId);
                response.sendRedirect("/patient/home");
            }
            case "DOCTOR" -> response.sendRedirect("/doctor/main");
            case "NURSE" -> response.sendRedirect("/nurse/main");
            default -> response.sendRedirect("/login?error=role");
        }
    }
}

