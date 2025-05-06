package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.AlertSetting;
import com.example.hospitalemr.service.AlertSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alertSetting")
public class AlertSettingController {

    private final AlertSettingService service;

    public AlertSettingController(AlertSettingService service) {
        this.service = service;
    }

    // 환자 설정 조회
    @GetMapping("/{patientId}")
    public ResponseEntity<AlertSetting> getByPatient(@PathVariable Long patientId) {
        AlertSetting setting = service.loadOrCreate(patientId);
        return ResponseEntity.ok(setting);
    }

    // 알림 설정 저장 및 업데이트
    @PostMapping("/{patientId}")
    public ResponseEntity<AlertSetting> update(
            @PathVariable Long patientId,
            @RequestParam boolean enabled,
            @RequestParam int hoursBefore,
            @RequestParam int minutesBefore) {

        AlertSetting s = service.loadOrCreate(patientId);
        s.setEnabled(enabled);
        s.setHoursBefore(hoursBefore);
        s.setMinutesBefore(minutesBefore);
        AlertSetting saved = service.save(s);
        return ResponseEntity.ok(saved);
    }
}
