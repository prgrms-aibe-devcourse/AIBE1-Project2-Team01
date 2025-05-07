package org.sunday.projectpop.service.llm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class LLMClient {

    @Value("${llm.api.key}")
    private String apiKey;

    @Value("${llm.api.url}")
    private String apiUrl;

    private WebClient createWebClient() {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> feedback(String content) {
        return createWebClient()
                .post()
                .uri("?key=%s".formatted(apiKey))
                .bodyValue(buildFeedbackPrompt(content))
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> response.candidates.get(0).content.parts.get(0).text);
    }

    private GeminiRequest buildFeedbackPrompt(String content) {
        String prompt = """
                당신은 실무경험이 10년 이상인 풀스택 개발자입니다.
                다음은 사용자의 포트폴리오 요약 내용입니다.
                아래 항목에 따라 구체적인 피드백을 각 300자 내외의 한글평문으로 작성. 미사여구 제외.
                
                1. 기술적 강점
                2. 보완할 점
                3. 구현 수준 평가
                4. 문서화 및 표현력
                5. 실무 연계성
                6. 추천 개선 방향
                
                포트폴리오 요약: %s
                """.formatted(content);
        return new GeminiRequest(
                List.of(new GeminiRequest.Content("user",
                        List.of(new GeminiRequest.Part(prompt)))));
    }


    public Mono<String> summarize(String content, String type) {
        return createWebClient()
                .post()
                .uri("?key=%s".formatted(apiKey))
                .bodyValue(buildSummaryPrompt(content, type))
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> response.candidates.get(0).content.parts.get(0).text);
    }

    private GeminiRequest buildSummaryPrompt(String content, String type) {
        String prompt = switch (type) {
            case "github" ->
                    "다음은 GitHub 저장소에서 추출한 주요 코드 파일과 README.md의 내용입니다. 이 프로젝트의 핵심 기능, 주요 기술 스택, 아키텍처 구성 등을 요약.기술적인 핵심만 300자 이내의 한글평문으로 정리. 미사여구 없이 간결하게 작성. %s".formatted(content);
            case "file" ->
                    "다음은 사용자가 제출한 포트폴리오 문서들의 주요 텍스트입니다. 문서에 담긴 핵심 기술 경험, 프로젝트 내용과 강조점, 문제 해결 방식 주요 내용과 강조점 기여 내용 등을 300자 이내의 한글평문으로 정리. 미사여구 없이 간결하게 핵심 내용만 전달되게 작성. %s".formatted(content);
            case "combined" ->
                    "다음은 사용자의 GitHub 저장소 내용과 제출 문서를 종합한 텍스트입니다. 전체 포트폴리오를 기반으로 핵심 기술 역량, 프로젝트의 목적 및 특징을 500자 이내의 한글평문으로 정리. 불필요한 미사여구 없이 읽는 사람이 한눈에 이해할 수 있게 간결하게 명확하게 작성. %s".formatted(content);
            default -> "";
        };
        return new GeminiRequest(
                List.of(new GeminiRequest.Content("user",
                        List.of(new GeminiRequest.Part(prompt)))));
    }

    public record GeminiRequest(List<Content> contents) {
        public record Content(String role, List<Part> parts) {
        }

        public record Part(String text) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeminiResponse(List<Candidate> candidates) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Candidate(Content content) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Content(List<Part> parts) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Part(String text) {
        }
    }
}