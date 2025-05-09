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


    public Mono<String> feedback(String content, boolean isFirst) {
        GeminiRequest request = isFirst ? buildFirstFeedbackPrompt(content) : buildFeedbackPrompt(content);
        return createWebClient()
                .post()
                .uri("?key=%s".formatted(apiKey))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> response.candidates.get(0).content.parts.get(0).text);
    }

    private GeminiRequest buildFirstFeedbackPrompt(String content) {
        String prompt = """
                당신은 실무경험이 10년 이상인 개발자입니다.
                다음은 한 사용자의 포트폴리오 설명, 회고 내용, 포트폴리오 요약입니다.
                아래 항목에 따라 구체적인 피드백을 300자 내외의 한글 평문으로 작성. 미사여구 제외.
                
                1. 기술적 강점
                2. 보완할 점
                3. 구현 수준 평가
                4. 문서화 및 표현력
                5. 실무 연계성
                6. 추천 개선 방향
                
                내용: %s
                """.formatted(content);
        return new GeminiRequest(
                List.of(new GeminiRequest.Content("user",
                        List.of(new GeminiRequest.Part(prompt)))));
    }

    private GeminiRequest buildFeedbackPrompt(String content) {
        String prompt = """
                당신은 실무 경험 10년 이상의 개발자입니다.
                다음은 한 사용자의 포트폴리오 설명, 회고 내용, 포트폴리오 요약, 그리고 이전 피드백 내용입니다. \s
                사용자는 이전 피드백을 바탕으로 일부 개선 작업을 진행했습니다. 이를 바탕으로 보완 피드백을 아래 항목에 따라 작성해주세요. 300자 내외. 미사여구 제외.
              
                아래 항목에 따라 보완 피드백 작성:
                1. 이전 피드백 반영 여부 및 변화 평가
                2. 개선된 부분의 완성도
                3. 아직 부족하거나 새롭게 보이는 문제점
                4. 향후 보완/심화하면 좋을 방향
                5. 실무 연계성 변화 (실무 수준에 가까워졌는지)
                6. 종합 평가 (전과 비교하여)
                
                내용: %s
                """.formatted(content);
        return new GeminiRequest(
                List.of(new GeminiRequest.Content("user",
                        List.of(new GeminiRequest.Part(prompt)))));
    }

    private GeminiRequest buildFirstFeedbackPromptByHR(String content) {
        String prompt = """
                당신은 실무 경력 10년 이상의 채용담당자입니다.
                
                다음은 한 지원자의 포트폴리오 설명, 회고 내용, 포트폴리오 요약입니다. \s
                채용자 입장에서 아래 항목에 따라 피드백을 300자 내외의 한글 평문으로 작성해주세요. \s
                형식적인 표현 없이 구체적이고 실질적인 조언을 포함해 주세요.
                
                아래 항목에 따라 피드백 작성:
                1. 인상 깊은 강점 및 표현력 \s
                2. 포지션 적합도 및 실무 연계성 \s
                3. 개선이 필요한 부분 (내용, 표현, 구성 등) \s
                4. 성장 가능성 및 학습 태도에 대한 인상 \s
                5. 문서화 또는 커뮤니케이션 측면의 보완점 \s
                6. 종합 평가 및 추천 사항
               
                내용: %s
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