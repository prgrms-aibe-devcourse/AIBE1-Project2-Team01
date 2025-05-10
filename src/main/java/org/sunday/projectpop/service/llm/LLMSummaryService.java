package org.sunday.projectpop.service.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.GitHubSummaryDTO;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class LLMSummaryService {

    private final LLMClient llmClient;

/*    public Mono<String> summarizeGithubDTO(GitHubSummaryDTO dto) {
        String githubText = """
                [디렉토리 구조] %s
                [언어] %s
                [코드 구조] %s
                [README 요약] %s
                [최근 커밋] %s
                """.formatted(dto.getDirectoryTree(), dto.getLanguages(), dto.getCodeStructure(), dto.getReadmeSummary(), dto.getCommitSummary());
        return llmClient.summarize(githubText, "githubRepo")
                .onErrorResume(e -> {
                    log.severe("GitHub 요약 실패: " + e.getMessage());
                    return Mono.just("GitHubRepo 요약 실패");
                });
    }*/

    public Mono<String> summarizeReadme(String readme) {
//        log.info("요약전 readme = " + readme);
        return llmClient.summarize(readme, "readme")
                .onErrorResume(e -> {
                    log.severe("README 요약 실패: " + e.getMessage());
                    return Mono.just("README 요약 실패");
                });
    }


    public Mono<String> summarizeGithubText(List<String> githubText) {
        if (githubText == null || githubText.isEmpty()) return Mono.just("");

        String combined = String.join("\n", githubText);
        return llmClient.summarize(combined, "github")
                .onErrorResume(e -> {
                    log.severe("GitHub 요약 실패: " + e.getMessage());
                    return Mono.just("GitHub 요약 실패");
                });
    }

    public Mono<String> summarizeFileText(List<String> fileText) {
        if (fileText == null || fileText.isEmpty()) return Mono.just("");

        String combined = String.join("\n", fileText);
        return llmClient.summarize(combined, "file")
                .onErrorResume(e -> {
                    log.severe("파일 요약 실패: " + e.getMessage());
                    return Mono.just("파일 요약 실패");
                });
    }

    public Mono<String> summarizeCombinedText(String combined) {
        if (combined == null || combined.isEmpty()) return Mono.just("");

        return llmClient.summarize(combined, "combined")
                .onErrorResume(e -> {
                    log.severe("최종 요약 실패: " + e.getMessage());
                    return Mono.just("최종 요약 실패");
                });
    }

}
