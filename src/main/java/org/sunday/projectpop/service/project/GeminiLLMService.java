package org.sunday.projectpop.service.project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.GeminiResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class GeminiLLMService {

    @Value("${llm.api.key}")
    private String apiKey;

    private static final String LLM_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";



    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiResponse getGeneratedProject(String prompt) {
        try {
            String requestBody = """
                {
                  "contents": [{
                    "parts": [{
                      "text": "%s"
                    }]
                  }]
                }
                """.formatted(prompt.replace("\"", "\\\""));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LLM_API_URL + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());

// 🔍 로그로 전체 응답 확인
            System.out.println("Gemini 응답 원문: " + response.body());

// ✅ candidates 체크
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new RuntimeException("Gemini 응답 실패: candidates가 비어 있음");
            }

// ✅ parts 체크
            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (!parts.isArray() || parts.isEmpty()) {
                throw new RuntimeException("Gemini 응답 실패: parts가 비어 있음");
            }

// ✅ 텍스트 추출
            String jsonText = parts.get(0).path("text").asText();
            if (jsonText.startsWith("```json") || jsonText.startsWith("```")) {
                jsonText = jsonText.replaceFirst("(?s)^```(?:json)?\\s*", ""); // 시작 ```json 또는 ```
                jsonText = jsonText.replaceFirst("\\s*```$", ""); // 끝 ```
            }
            System.out.println("Gemini 응답 JSON 텍스트: " + jsonText);

// ✅ JSON → DTO 변환
            return objectMapper.readValue(jsonText, GeminiResponse.class);


        } catch (Exception e) {
            throw new RuntimeException("Gemini 응답 실패: " + e.getMessage(), e);
        }
    }
}
