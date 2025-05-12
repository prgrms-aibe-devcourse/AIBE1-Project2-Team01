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

    // LLM 피드백 요청
    public Mono<String> feedback(String content, String type) {
        return createWebClient()
                .post()
                .uri("?key=%s".formatted(apiKey))
                .bodyValue(buildFeedbackPrompt(content, type))
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> response.candidates.get(0).content.parts.get(0).text);
    }

    // 피드백을 위한 프롬프트 생성
    private GeminiRequest buildFeedbackPrompt(String content, String type) {
        String prompt = switch (type) {
            case "first_develop" -> """
                    다음은 한 개발자가 제출한 포트폴리오 프로젝트의 요약 내용입니다. 이 프로젝트는 실제 개발 경험을 보여주기 위한 용도로 작성되었습니다. 아래 요약을 바탕으로 다음 조건에 따라 피드백을 작성해주세요.
                    
                    [피드백 목적]
                    - 제출자가 프로젝트를 더 잘 개선할 수 있도록 구체적이고 실용적인 조언 제공
                    - 기술적 완성도, 설계의 논리성, 문서의 명확성 등을 평가하고 보완점 제안
                    
                    [피드백 항목]
                    1. 프로젝트의 설계 및 개요에 대한 이해도
                    2. 주요 기능이 충분히 구현되었는지에 대한 판단
                    3. 기술 스택 선택의 적절성
                    4. 문서화 및 명세서 작성의 충실도
                    5. 보완하거나 발전시킬 수 있는 부분
                    6. 전체적인 인상과 제안
                    
                    [작성 방식]
                    - 각 항목별로 번호를 붙여 한글로 작성
                    - 장단점을 함께 제시
                    - 가능한 한 실무적 조언을 포함
                    - 추상적인 표현은 피하고 구체적 표현 사용
                    - 미사여구 제외 (시작시 '피드백' 금지)
                    
                    프로젝트 요약:
                    %s
                    """.formatted(content);
            case "develop" -> """
                    이전 피드백을 바탕으로 프로젝트 내용에 일부 변경을 추가하고, 노트 작성. 프로젝트에 대한 요약과 이전 피드백이 포함됨
                    
                    [추가된 내용]
                    - 노트 내용: 프로젝트 진행 상황과 작업 내용, 진행 과정에서 배운 점, 겪었던 어려움, 개선이 필요한 부분 등이 포함
                    
                    이러한 추가 사항을 바탕으로 피드백 제시. 아래 항목들에 따라 재평가. 작성방식 준수
                    
                    [피드백 요청 항목]
                    1. 노트(회고/작업) 내용이 프로젝트 진행에 적합한지
                    2. 추가된 노트(회고/작업) 내용이 잘 반영되었는지 및 다른 개선이 필요한 부분
                    3. 이전 피드백을 반영한 점이 잘 개선되었는지
                    4. 전체 프로젝트에 대한 종합적인 평가
                    
                    [작성 방식]
                    - 각 항목별로 번호를 붙여 작성 (중요!)
                    - 300자 이내의 한글로 작성 (글자수 표시 금지)
                    - 실무적인 조언을 포함하여 구체적으로 작성
                    - 내용이 부족하거나 추가해야 할 부분이 있다면 그에 대해 언급
                    - 미사여구 제외 (시작시 '피드백' 금지)
                    
                    내용: %s
                    """.formatted(content);
            case "hr" -> """                    
                    다음은 한 지원자의 포트폴리오 설명, 회고 내용, 포트폴리오 요약입니다. \s
                    채용자 입장에서 아래 항목에 따라 피드백을 500자 내외의 한글 평문으로 작성해주세요. \s
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
            default -> "";
        };
        return new GeminiRequest(
                List.of(new GeminiRequest.Content("user",
                        List.of(new GeminiRequest.Part(prompt)))));
    }

    // LLM 요약 요청
    public Mono<String> summarize(String content, String type) {
        return createWebClient()
                .post()
                .uri("?key=%s".formatted(apiKey))
                .bodyValue(buildSummaryPrompt(content, type))
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> response.candidates.get(0).content.parts.get(0).text);
    }

    // 요약을 위한 프롬프트 생성
    private GeminiRequest buildSummaryPrompt(String content, String type) {
        String prompt = switch (type) {
//            case "github" ->
//                    "다음은 GitHub 저장소에서 추출한 주요 코드 파일과 README.md의 내용입니다. 이 프로젝트의 핵심 기능, 주요 기술 스택, 아키텍처 구성 등을 요약.기술적인 핵심만 300자 이내의 한글평문으로 정리. 미사여구 없이 간결하게 작성. %s".formatted(content);
            case "github" -> """
                     다음은 GitHub 프로젝트의 다양한 정보를 수집한 것입니다. 이 정보를 바탕으로 프로젝트의 전반적인 내용을 이해하고, 피드백을 위한 요약 작성.
                    
                     [요약 목적]
                     - 프로젝트의 구성과 흐름을 빠르게 파악하고, 기술적인 피드백이나 개선점을 제시할 수 있도록 전체 내용을 요약
                    
                     [요약 항목]
                     1. 프로젝트 요약: 전체적인 목표와 기능 개요
                     2. 기술 스택 및 구조 설명: 사용된 언어, 프레임워크, 폴더 구조 설명
                     3. 코드의 주요 흐름 및 설계: 중심이 되는 코드 로직과 설계 포인트
                     4. 최근 개발 동향: 커밋 메시지를 기반으로 현재 프로젝트 상태나 변경 내역
                     5. 예상 피드백 포인트: 보완이 필요해 보이는 부분이나 개선 제안 (간단히)
                    
                     [요청 형식]
                     각 항목을 번호로 구분하여 3-5줄 이내로 일목요연하게 정리. 한글평문으로 작성. 미사여구 제외
                    
                     GitHub 분석 정보: %s
                    """.formatted(content);
            case "file" -> """
                    다음은 개발자가 포트폴리오로 제출한 문서입니다. 이 문서에는 프로젝트의 진행 현황, API 명세서, 시스템 아키텍처, 다이어그램, 구현 계획, 기술 스택 선택 이유, 그리고 기타 산출물 등이 포함되어 있을 수 있습니다.
                    
                    [요약 항목]
                    1. 프로젝트 개요 및 목적
                       - 이 프로젝트는 어떤 문제를 해결하기 위한 것인가요?
                       - 주제와 핵심 목표는 무엇인가요?
                    2. 기술 구조 및 구성요소 설명
                       - 사용된 기술 스택은 무엇인가요?
                       - 주요 컴포넌트(API, 서비스, 모듈 등)의 역할과 구조는 어떻게 구성되어 있나요?
                    3. 구현 및 개발 진행도
                       - 개발 진행 상태는 어떤가요?
                       - 문서에 나타난 일정표, 그래프 등을 바탕으로 현재 진행 상황을 요약해주세요.
                    4. 설계 및 아키텍처 관련 특징
                       - 아키텍처 구성(계층, 패턴, 통신 등)은 어떤 식으로 설계되었나요?
                       - 문서에서 드러나는 설계적 강점이나 고민 흔적이 있다면 언급해주세요.
                    5. 기타 특이사항 또는 돋보이는 부분
                       - 팀 협업 요소, 테스트 전략, 배포 전략, UI/UX 고려사항 등 문서에 나타나는 기타 주목할 만한 점은 무엇인가요?
                    6. 요약 정리
                       - 위 항목들을 바탕으로 이 포트폴리오의 전반적인 인상 및 이해한 내용을 간략하게 요약해 주세요.
                    [요청 형식]
                    항목별로 번호를 붙여 간결하게 요약. 항목이 누락되어 있다면 "정보 없음"이라고 표시. 한글평문으로 작성. 미사여구 제외
                    
                    문서 내용: %s
                    """.formatted(content);
            case "combined" -> """
                    다음은 GitHub 프로젝트의 요약 내용과 포트폴리오 문서의 요약 내용입니다. 이 두 자료를 기반으로 아래 항목을 기준으로 최종 요약을 작성해주세요.
                    
                    [요약 목적]
                    - 프로젝트의 전체 내용을 빠르게 이해하고, 효과적인 피드백을 제공할 수 있도록 내용을 정리합니다.
                    
                    [요약 항목]
                    1. 프로젝트 개요: 어떤 프로젝트인지 간결하게 설명
                    2. 주요 기능 및 특징: 사용자가 사용할 수 있는 핵심 기능
                    3. 사용된 기술 스택: 언어나 프레임워크, 주요 라이브러리 등
                    4. 개발 및 설계의 강점: 구조적 장점, 설계 철학, 개발 방식에서 드러나는 특징
                    5. 진행 상황 및 기여도: 전체 개발 진행 상황 및 개인 또는 팀 기여 정도
                    6. 기타 주목할 요소: 문서, 아키텍처, 다이어그램 등에서 드러나는 특이 사항
                    
                    [요청 형식]
                    항목별로 번호를 붙여 간결하게 요약. 항목이 누락되어 있다면 "정보 없음"이라고 표시. 한글 평문으로 작성. 미사여구 제외.
                    
                    내용: %s
                    """.formatted(content);
            case "readme" -> """
                    다음은 GitHub 프로젝트의 README.md 파일입니다. 이 파일을 기반으로 아래 항목을 기준으로 요약해주세요.
                    
                    [요약 목적]
                    - 프로젝트에 대해 빠르게 이해하고, 효과적인 피드백을 줄 수 있도록 내용을 정리하고 함.
                    
                    [요약 항목]
                    1. 프로젝트 개요: 어떤 프로젝트인지 간결하게 설명
                    2. 주요 기능 및 특징: 사용자가 사용할 수 있는 핵심 기능
                    3. 사용된 기술 스택: 언어나 프레임워크, 주요 라이브러리 등
                    4. 실행 방법 요약: 설치 또는 실행 지침이 있다면 요약
                    5. 기여 방법 및 기타 특이사항 (있다면)
                    
                    [요청 형식]
                    항목별로 번호를 붙여 간결하게 요약. 항목이 누락되어 있다면 "정보 없음"이라고 표시. 한글평문으로 작성. 미사여구 제외
                    
                    README: %s
                    """.formatted(content);
            default -> "";
        };
        return new GeminiRequest(
                List.of(new GeminiRequest.Content("user",
                        List.of(new GeminiRequest.Part(prompt)))));
    }

    // Gemini 요청 객체
    public record GeminiRequest(List<Content> contents) {
        public record Content(String role, List<Part> parts) {
        }

        public record Part(String text) {
        }
    }

    // Gemini 응답 객체
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