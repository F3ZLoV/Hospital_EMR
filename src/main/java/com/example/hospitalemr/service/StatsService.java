package com.example.hospitalemr.service;

import com.example.hospitalemr.repository.MedicalVisitRepository;
import com.example.hospitalemr.repository.PatientRepository;
import com.example.hospitalemr.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final MedicalVisitRepository visitRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final PatientRepository patientRepo;

    public StatsService(MedicalVisitRepository visitRepo,
                        PrescriptionRepository prescriptionRepo,
                        PatientRepository patientRepo) {
        this.visitRepo = visitRepo;
        this.prescriptionRepo = prescriptionRepo;
        this.patientRepo = patientRepo;
    }

    public List<Map<String, Object>> getDailyVisitCounts() {
        List<Object[]> raw = visitRepo.findDailyVisitCountsNative();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : raw) {
            result.add(Map.of("date", row[0].toString(), "count", ((Number) row[1]).intValue()));
        }
        return result;
    }

    public List<Map<String, Object>> getTopPrescribedDrugs() {
        List<Object[]> rawResult = prescriptionRepo.findTopDrugsNative();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rawResult) {
            result.add(Map.of("drugName", row[0], "count", ((Number) row[1]).intValue()));
        }
        return result;
    }

    public List<Map<String, Object>> getAgeGroupDistribution() {
        LocalDate today = LocalDate.now();

        return patientRepo.findAll().stream()
                .map(p -> {
                    LocalDate birth = p.getDate_of_birth();
                    int age = today.getYear() - birth.getYear();
                    if (today.getDayOfYear() < birth.getDayOfYear()) {
                        age--;
                    }
                    return age;
                })
                .filter(age -> age >= 0 && age < 120)
                .map(age -> (age / 10) * 10)
                .collect(Collectors.groupingBy(
                        ageGroup -> ageGroup + "대",
                        TreeMap::new,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("range", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
