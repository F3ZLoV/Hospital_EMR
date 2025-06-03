package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.UserAccount;
import com.example.hospitalemr.repository.UserAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private UserAccountRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    public AuthController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String registerForm() { return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String role,
                           @RequestParam Long linkedId) {
        if (userRepo.findByUsername(username).isPresent()) {
            return "redirect:/register?error"; // 중복 예외
        }
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserAccount.Role.valueOf(role));
        user.setLinkedId(linkedId);
        userRepo.save(user);
        return "redirect:/login";
    }
}

