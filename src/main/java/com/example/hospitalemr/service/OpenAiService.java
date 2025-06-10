//package com.example.hospitalemr.service;
//
//import okhttp3.*;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OpenAiService {
//    private final String API_KEY = "sk-..."; // 실제 API키로 변경
//    private final String API_URL = "https://api.openai.com/v1/chat/completions";
//
//    public String askGpt(String prompt) {
//        OkHttpClient client = new OkHttpClient();
//
//        String reqBody = "{\n" +
//                "  \"model\": \"gpt-3.5-turbo\",\n" +
//                "  \"messages\": [\n" +
//                "    {\"role\": \"user\", \"content\": \"" + prompt.replace("\"", "\\\"") + "\"}\n" +
//                "  ]\n" +
//                "}";
//        Request request = new Request.Builder()
//                .url(API_URL)
//                .header("Authorization", "Bearer " + API_KEY)
//                .header("Content-Type", "application/json")
//                .post(RequestBody.create(reqBody, MediaType.get("application/json")))
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String json = response.body().string();
//                // 간단 파싱 (생략시 json 전체 출력됨)
//                int idx = json.indexOf("\"content\":\"");
//                if (idx != -1) {
//                    String cut = json.substring(idx + 11);
//                    return cut.split("\"")[0].replace("\\n", "\n");
//                }
//                return "AI 응답 파싱 실패";
//            } else {
//                return "AI 응답 실패: " + response.code();
//            }
//        } catch (Exception e) {
//            return "AI 서버 에러: " + e.getMessage();
//        }
//    }
//}
