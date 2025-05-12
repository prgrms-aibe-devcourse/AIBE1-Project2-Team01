package org.sunday.projectpop.service.feedback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.sunday.projectpop.exceptions.GitHubManagementException;
import org.sunday.projectpop.model.dto.GitHubRepoInfo;
import org.sunday.projectpop.model.dto.GitHubSummaryDTO;
import org.sunday.projectpop.service.analyzer.CodeAnalyzer;
import org.sunday.projectpop.service.llm.LLMSummaryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log
public class GitHubServiceImpl implements GitHubService {

    private final LLMSummaryService llmSummaryService;
    private final CodeAnalyzer codeAnalyzer;

    @Value("${github.token}")
    private String gitHubToken;

    private final ObjectMapper objectMapper;

    private WebClient createWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("Authorization", "Bearer " + gitHubToken)
                .build();
    }

    public boolean isAfterLastCommit(String url, Instant time) {
        GitHubRepoInfo repoInfo = parseGitHubUrl(url);
        Instant updatedAt = fetchUpdatedAt(repoInfo).block();
        return updatedAt != null && updatedAt.isAfter(time);
    }

    // 종합 정리 & LLM 요청용 DTO 생성
    public Mono<GitHubSummaryDTO> summarizeProject(String url) {
        log.info("url = " + url);
        GitHubRepoInfo repoInfo = parseGitHubUrl(url);

        Mono<String> directoryTree = fetchDirectoryTree(repoInfo);
        Mono<String> languages = fetchLanguages(repoInfo);
        Mono<String> codeSummary = analyzeCodeStructure(repoInfo, "");
        Mono<String> readmeSummary = fetchAndSummarizeReadme(repoInfo);
        Mono<String> commitSummary = fetchRecentCommitMessage(repoInfo);
        Mono<String> ciCd = analyzeCiCd(repoInfo);

        return Mono.zip(
                        directoryTree, languages, codeSummary, readmeSummary, commitSummary, ciCd
                )
                .publishOn(Schedulers.boundedElastic())
                .map(tuple -> {
                    GitHubSummaryDTO dto = new GitHubSummaryDTO();
                    dto.setDirectoryTree(tuple.getT1());
                    dto.setLanguages(tuple.getT2());
                    dto.setCodeStructure(tuple.getT3());
                    dto.setReadmeSummary(tuple.getT4());
                    dto.setCommitSummary(tuple.getT5());
                    dto.setCiCd(tuple.getT6());
//                    dto.setFinalSummary(llmSummaryService.summarizeGithubDTO(dto).block());
                    return dto;
                });
    }

    // 디렉토리 구조 분석 - 트리 재귀 탐색
    private Mono<String> fetchDirectoryTree(GitHubRepoInfo repoInfo) {
        return getDefaultBranchSha(repoInfo)
                .flatMap(sha -> createWebClient().get()
                        .uri("/repos/{owner}/{repo}/git/trees/{sha}?recursive=true",
                                repoInfo.owner(), repoInfo.repo(), sha)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .map(node -> {
                            StringBuilder sb = new StringBuilder();
                            for (JsonNode item : node.get("tree")) {
                                String path = item.get("path").asText();
                                String type = item.get("type").asText();
                                if ("tree".equals(type)) {
                                    sb.append("📁 ").append(path).append("\n");
                                } else {
                                    sb.append("📄 ").append(path).append("\n");
                                }
                            }
                            return sb.toString();
                        }))
                .onErrorReturn("디렉토리 구조 분석 실패");
    }

    // 기본 브랜치의 SHA 가져오기
    private Mono<String> getDefaultBranchSha(GitHubRepoInfo repoInfo) {
        return createWebClient().get()
                .uri("/repos/{owner}/{repo}", repoInfo.owner(), repoInfo.repo())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(json -> {
                    String defaultBranch = json.get("default_branch").asText();
                    return createWebClient().get()
                            .uri("/repos/{owner}/{repo}/branches/{branch}",
                                    repoInfo.owner(), repoInfo.repo(), defaultBranch)
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .map(branch -> branch.get("commit").get("sha").asText());
                })
                .onErrorReturn("기본 브랜치 없음");
    }


    // 코드 분석
    private Mono<String> analyzeCodeStructure(GitHubRepoInfo repoInfo, String path) {
        return createWebClient().get()
                .uri("/repos/{owner}/{repo}/contents/{path}",
                        repoInfo.owner(), repoInfo.repo(), path)
                .retrieve()
                .bodyToMono(JsonNode[].class)
                .flatMapMany(Flux::fromArray) // JsonNode[] -> Flux<JsonNode>
                .flatMap(node -> {
                    String type = node.get("type").asText();
                    String filePath = node.get("path").asText();

                    if ("file".equals(type)) {
                        // 파일 분석
                        return fetchFileContent(repoInfo, filePath)
                                .map(content -> {
                                    String analysis = codeAnalyzer.analyzeFile(filePath, content);
                                    return "🔍 파일: " + filePath + "\n분석 결과:\n" + analysis + "\n\n";
                                });
                    } else if ("dir".equals(type)) {
                        // 디렉토리 재귀 탐색
                        return analyzeCodeStructure(repoInfo, filePath)
                                .map(subAnalysis -> "📂 디렉토리: " + filePath + "\n" + subAnalysis);
                    }
                    return Mono.empty();
                })
                .collectList()
                .map(list -> String.join("", list))
                .onErrorResume(e -> {
                    log.severe("코드 분석 실패: " + e.getMessage());
                    return Mono.just("⚠️ 코드 분석 실패: " + path);
                });
    }

    // 파일 내용 base64 디코딩
    private Mono<String> fetchFileContent(GitHubRepoInfo repoInfo, String path) {
        return createWebClient().get()
                .uri("/repos/{owner}/{repo}/contents/{path}", repoInfo.owner(), repoInfo.repo(), path)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("content").asText())
                .map(Base64.getMimeDecoder()::decode)
                .map(String::new)
                .onErrorReturn("파일 읽기 실패");
    }

    // CI/CD 파이프라인 분석
    private Mono<String> analyzeCiCd(GitHubRepoInfo repoInfo) {
        return createWebClient().get()
                .uri("/repose/{owner}/{repo}/contents/.github/workflows", repoInfo.owner(), repoInfo.repo())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMapMany(Flux::fromIterable)
                .map(node -> node.get("name").asText())
                .collectList()
                .map(workflow -> "CI/CD 워크플로우 파일: " + workflow)
                .onErrorReturn("CI/CD 없음 또는 분석 실패");
    }

    // README 요약
    private Mono<String> fetchAndSummarizeReadme(GitHubRepoInfo repoInfo) {
        return createWebClient().get()
                .uri("/repose/{owner}/{repo}/readme", repoInfo.owner(), repoInfo.repo())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("content").asText())
                .map(Base64.getMimeDecoder()::decode)
                .map(String::new)
                .flatMap(this::summarizeMarkdown)
                .onErrorReturn("README 없음 또는 분석 실패");
    }

    // 마크다운 요약
    private Mono<String> summarizeMarkdown(String markdown) {
        return llmSummaryService.summarizeReadme(markdown);
    }

    // 커밋 메시지 분석
    private Mono<String> fetchRecentCommitMessage(GitHubRepoInfo repoInfo) {
        return createWebClient().get()
                .uri("/repos/{owner}/{repo}/commits", repoInfo.owner(), repoInfo.repo())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    StringBuilder sb = new StringBuilder();
                    for (JsonNode commit : json) {
                        sb.append("♦︎ ").append(commit.get("commit").get("message").asText()).append("\n");
                    }
                    return sb.toString();
                })
                .onErrorReturn("커밋 메시지 없음");
    }

    // 마지막 커밋 시간
    private Mono<Instant> fetchUpdatedAt(GitHubRepoInfo repoInfo) {
        return createWebClient().get()
                .uri("/repos/{owner}/{repo}", repoInfo.owner(), repoInfo.repo())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    String updatedAt = (String) response.get("updated_at");
                    return Instant.parse(updatedAt);
                });
    }

    // 언어 정보
    private Mono<String> fetchLanguages(GitHubRepoInfo repoInfo) {
        return createWebClient().get()
                .uri("/repos/{owner}/{repo}/languages", repoInfo.owner(), repoInfo.repo())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(JsonNode::toPrettyString)
                .onErrorReturn("언어 정보 없음");
    }

    // 깃허브 URL 파싱
    private GitHubRepoInfo parseGitHubUrl(String url) {
        try {
            URI uri = new URI(url);
            String[] parts = uri.getPath().split("/");
            return new GitHubRepoInfo(parts[1], parts[2]);

        } catch (Exception e) {
            throw new GitHubManagementException("유효하지 않은 GitHub URL입니다.");
        }
    }
}

