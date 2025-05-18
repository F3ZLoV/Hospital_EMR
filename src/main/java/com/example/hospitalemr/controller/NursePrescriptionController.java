package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.MedicalVisit;
import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.domain.Prescription;
import com.example.hospitalemr.repository.MedicalVisitRepository;
import com.example.hospitalemr.repository.PatientRepository;
import com.example.hospitalemr.repository.PrescriptionRepository;
import com.example.hospitalemr.service.PrescriptionPrintService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/nurse/prescriptions")
public class NursePrescriptionController {

    private final MedicalVisitRepository visitRepo;
    private final PatientRepository patientRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final PrescriptionPrintService printService;

    public NursePrescriptionController(
            MedicalVisitRepository visitRepo,
            PatientRepository patientRepo,
            PrescriptionRepository prescriptionRepo,
            PrescriptionPrintService printService
    ) {
        this.visitRepo = visitRepo;
        this.patientRepo = patientRepo;
        this.prescriptionRepo = prescriptionRepo;
        this.printService = printService;
    }

//    @GetMapping("/print/{visitId}")
//    public void generatePrescriptionDocx(@PathVariable Long visitId, HttpServletResponse response) throws Exception {
//        // 진료 방문, 환자, 처방 정보 가져오기
//        MedicalVisit visit = visitRepo.findById(visitId).orElseThrow();
//        Patient patient = visit.getPatient();
//        List<Prescription> prescriptions = prescriptionRepo.findByVisitId(visitId);
//
//        // docx 생성
//        var docxStream = printService.generatePrescriptionDocx(patient, prescriptions);
//
//        // HTTP 응답으로 Word 파일 전송
//        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
//        response.setHeader("Content-Disposition", "inline; filename=prescription.docx");
//        response.getOutputStream().write(docxStream.toByteArray());
//        response.getOutputStream().flush();
//    }
}
