package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.EntExamination;
import com.example.hospitalemr.repository.EntExaminationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/examination")
public class ExaminationController {
    private final EntExaminationRepository entExaminationRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final EntExaminationRepository repo;

    public ExaminationController(EntExaminationRepository repo, EntExaminationRepository entExaminationRepository) {
        this.repo = repo;
        this.entExaminationRepository = entExaminationRepository;
    }

    @PostMapping(value = "/saveAll", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> saveAll(MultipartHttpServletRequest request) throws IOException {
        String visitIdStr = request.getParameter("visitId");
        if (visitIdStr == null) {
            return Map.of("success", false, "message", "visitId 누락");
        }
        Long visitId = Long.valueOf(visitIdStr);

        int maxIdx = request.getParameterMap().keySet().stream()
                .filter(k -> k.startsWith("exams["))
                .mapToInt(k -> Integer.parseInt(k.substring(6, k.indexOf(']', 6))))
                .max().orElse(-1);

        if (maxIdx < 0) {
            return Map.of("success", true, "message", "저장할 검사 결과 없음");
        }

        Path baseDir = Paths.get(uploadDir, "exam_images");
        Files.createDirectories(baseDir);

        for (int i = 0; i <= maxIdx; i++) {
            String type    = request.getParameter("exams[" + i + "].examType");
            String summary = request.getParameter("exams[" + i + "].examSummary");
            String dateStr = request.getParameter("exams[" + i + "].examDate");
            if ((type == null && summary == null) || dateStr == null) {
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
                    Path dest = baseDir.resolve(filename);
                    Files.copy(mf.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
                    e.getExamMediaList().add("/uploads/exam_images/" + filename);
                }
            }

            repo.save(e);
        }

        return Map.of("success", true);
    }

    @GetMapping("/media")
    public List<String> getMediaByVisit(@RequestParam Long visitId) {
        return repo.findByVisitId(visitId).stream()
                .flatMap(e -> e.getExamMediaList().stream())
                .collect(Collectors.toList());
    }

    @GetMapping("/history")
    public ResponseEntity<List<ExaminationDto>> getExamHistory(@RequestParam Long visitId) {
        List<EntExamination> exams = entExaminationRepository.findByVisitId(visitId);
        List<ExaminationDto> dtos = exams.stream()
                .map(e -> new ExaminationDto(
                        e.getExamDate().toString(),
                        e.getExamType(),
                        e.getExamSummary(),
                        e.getExamMediaList()    // 엔티티에 mediaList(List<String>)가 있다고 가정
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    public static class ExaminationDto {
        private String examDate;
        private String examType;
        private String examSummary;
        private List<String> mediaList;

        public ExaminationDto(String examDate, String examType, String examSummary, List<String> mediaList) {
            this.examDate    = examDate;
            this.examType    = examType;
            this.examSummary = examSummary;
            this.mediaList   = mediaList;
        }
        // getters / setters 생략

        public String getExamDate() {
            return examDate;
        }

        public void setExamDate(String examDate) {
            this.examDate = examDate;
        }

        public String getExamType() {
            return examType;
        }

        public void setExamType(String examType) {
            this.examType = examType;
        }

        public String getExamSummary() {
            return examSummary;
        }

        public void setExamSummary(String examSummary) {
            this.examSummary = examSummary;
        }

        public List<String> getMediaList() {
            return mediaList;
        }

        public void setMediaList(List<String> mediaList) {
            this.mediaList = mediaList;
        }
    }
}
