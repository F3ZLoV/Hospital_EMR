package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.EntExamination;
import com.example.hospitalemr.repository.EntExaminationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/examination")
public class ExaminationController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final EntExaminationRepository repo;

    public ExaminationController(EntExaminationRepository repo) {
        this.repo = repo;
    }

    @PostMapping(value = "/saveAll", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> saveAll(MultipartHttpServletRequest request) throws IOException {
        String visitIdStr = request.getParameter("visitId");
        if (visitIdStr == null) {
            return Map.of("success", false, "message", "visitId 누락");
        }
        Long visitId = Long.valueOf(visitIdStr);

        // 파라미터에서 몇 건이 왔는지 확인 (exams[0].examType 이 존재하는 인덱스 수만큼)
        // 편의상 파라미터 키들에서 인덱스 최대값을 찾아냅니다.
        int maxIdx = request.getParameterMap().keySet().stream()
                .filter(k -> k.startsWith("exams["))
                .mapToInt(k -> Integer.parseInt(k.substring(6, k.indexOf(']',6))))
                .max().orElse(-1);
        if (maxIdx < 0) {
            return Map.of("success", true, "message", "저장할 검사 결과 없음");
        }

        for (int i = 0; i <= maxIdx; i++) {
            String type = request.getParameter("exams[" + i + "].examType");
            String summary = request.getParameter("exams[" + i + "].examSummary");
            String dateStr = request.getParameter("exams[" + i + "].examDate");
            if ((type == null && summary == null) || dateStr == null) {
                // 이 인덱스는 건너뜀
                continue;
            }

            EntExamination e = new EntExamination();
            e.setVisitId(visitId);
            e.setExamType(type);
            e.setExamDate(LocalDate.parse(dateStr));
            e.setExamSummary(summary);
            List<MultipartFile> files = request.getFiles("exams[" + i + "].mediaFiles");
            if (!files.isEmpty()) {
                for (MultipartFile mf : files) {
                    if (mf.isEmpty()) continue;
                    String filename = UUID.randomUUID() + "_" + mf.getOriginalFilename();
                    Path dest = Paths.get(uploadDir).resolve("exam_images").resolve(filename);
                    Files.createDirectories(dest.getParent());
                    Files.copy(mf.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
                    e.getExamMediaList().add("/uploads/exam_images/" + filename);
                }
            }

            repo.save(e);
        }

        return Map.of("success", true);
    }
}
