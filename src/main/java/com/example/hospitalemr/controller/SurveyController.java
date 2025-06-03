package com.example.hospitalemr.controller;

import com.example.hospitalemr.domain.SurveyAnswer;
import com.example.hospitalemr.domain.SurveyQuestion;
import com.example.hospitalemr.repository.SurveyAnswerRepository;
import com.example.hospitalemr.repository.SurveyQuestionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/survey")
public class SurveyController {
    private final SurveyQuestionRepository questionRepo;
    private final SurveyAnswerRepository answerRepo;

    @Autowired
    public SurveyController(SurveyQuestionRepository questionRepo, SurveyAnswerRepository answerRepo) {
        this.questionRepo = questionRepo;
        this.answerRepo = answerRepo;
    }

    @GetMapping("/questions")
    @ResponseBody
    public List<Map<String, Object>> getSurveyQuestions() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SurveyQuestion q : questionRepo.findAll()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId());
            map.put("questionText", q.getQuestionText());
            map.put("questionType", q.getQuestionType());
            map.put("choiceOptions", q.getChoiceOptions() == null ? "" : q.getChoiceOptions());
            list.add(map);
        }
        return list;
    }



    @PostMapping("/submit")
    @ResponseBody
    public Map<String, Object> submitSurvey(HttpServletRequest req) {
        Integer patientId = Integer.parseInt(req.getParameter("patientId"));
        LocalDateTime now = LocalDateTime.now();

        List<SurveyQuestion> questions = questionRepo.findAll();
        for (SurveyQuestion q : questions) {
            String param = req.getParameter("answer_" + q.getId());
            if (param == null) continue; // 미답변은 저장 안함
            SurveyAnswer answer = new SurveyAnswer();
            answer.setPatientId(patientId);
            answer.setQuestion(q);
            answer.setAnswerText(param);
            answer.setSurveyDate(now);
            answerRepo.save(answer);
        }
        return Map.of("success", true);
    }


    @GetMapping("/latest")
    @ResponseBody
    public List<Map<String, Object>> getLatestSurvey(@RequestParam("patientId") Integer patientId) {
        // surveyDate 기준으로 내림차순 정렬 후 최신 날짜 1회분만 출력
        List<SurveyAnswer> all = answerRepo.findByPatientIdOrderBySurveyDateDesc(patientId);
        if (all.isEmpty()) return Collections.emptyList();
        LocalDateTime latest = all.get(0).getSurveyDate();
        List<SurveyAnswer> answers = answerRepo.findByPatientIdAndSurveyDate(patientId, latest);

        List<Map<String, Object>> result = new ArrayList<>();
        for (SurveyAnswer ans : answers) {
            result.add(Map.of(
                    "question", ans.getQuestion().getQuestionText(),
                    "answer", ans.getAnswerText()
            ));
        }
        return result;
    }

}
