package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.MedicalVisit;
import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.domain.Prescription;
import com.example.hospitalemr.repository.MedicalVisitRepository;
import com.example.hospitalemr.repository.PatientRepository;
import com.example.hospitalemr.repository.PrescriptionRepository;
import com.example.hospitalemr.service.PrescriptionExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/nurse/prescriptions")
public class NursePrescriptionController {

    private final MedicalVisitRepository visitRepo;
    private final PatientRepository patientRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final PrescriptionExportService prescriptionExportService;

    public NursePrescriptionController(
            MedicalVisitRepository visitRepo,
            PatientRepository patientRepo,
            PrescriptionRepository prescriptionRepo, PrescriptionExportService prescriptionExportService) {
        this.visitRepo = visitRepo;
        this.patientRepo = patientRepo;
        this.prescriptionRepo = prescriptionRepo;
        this.prescriptionExportService = prescriptionExportService;
    }

    @GetMapping("/print/{visitId}")
    public void generatePrescriptionPDF(@PathVariable Long visitId, HttpServletResponse response) throws Exception {
        MedicalVisit visit = visitRepo.findById(visitId).orElseThrow();
        Patient patient = patientRepo.findById(visit.getPatientId()).orElseThrow();
        List<Prescription> prescriptions = prescriptionRepo.findByVisitId(visitId);

        // .docx 파일 생성
        byte[] docBytes = prescriptionExportService.generatePrescriptionDoc(patient, prescriptions);
        File tempDocx = File.createTempFile("prescription_", ".docx");
        try (FileOutputStream fos = new FileOutputStream(tempDocx)) {
            fos.write(docBytes);
        }

        // PDF 변환
        File pdfFile = prescriptionExportService.convertDocxToPdf(tempDocx);

        // PDF 브라우저 새탭에 띄우기
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=prescription.pdf");

        try (InputStream pdfIn = new FileInputStream(pdfFile)) {
            pdfIn.transferTo(response.getOutputStream());
        }

        // 임시 파일 삭제
        tempDocx.delete();
        pdfFile.delete();
    }



}
