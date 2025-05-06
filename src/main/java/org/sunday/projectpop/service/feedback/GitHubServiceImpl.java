package org.sunday.projectpop.service.feedback;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.activation.FileDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.sunday.projectpop.exceptions.GitHubManagementException;
import org.sunday.projectpop.model.dto.GitHubRepoInfo;

import java.net.URI;
import java.util.*;

@Service
@Log
@RequiredArgsConstructor
public class GitHubServiceImpl implements GitHubService {

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

    @Override
    public List<String> fetchAndConvertFiles(String url) {
        GitHubRepoInfo repoInfo = parseGitHubUrl(url);

        String readme = fetchReadme(repoInfo);
        Map<String, Integer> languages = fetchLanguages(repoInfo);
        List<String> targetExtentions = decideExtentions(languages);

        List<String> codeFiles = fetchImportantFiles(repoInfo, targetExtentions);

        List<String> result = new ArrayList<>();
        if (readme != null) {
//            log.info("readme = " + readme);
            result.add("[README.md]\n" + readme);
        } else {
            result.add("[README.md 없음] 해당 저장소에는 README.md 파일이 존재하지 않습니다.");
        }
        result.addAll(codeFiles);
        return result;
    }

    // 파일 구조 전체 조회 후, 주요 파일 10개 내용 가져오기
    private List<String> fetchImportantFiles(GitHubRepoInfo repoInfo, List<String> extentions) {
        try {
            // 전체 파일 목록
            String treeRes = createWebClient().get()
                    .uri("/repos/{owner}/{repo}/git/trees/main?recursive=true",
                            repoInfo.owner(), repoInfo.repo())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
//            log.info("treeRes = " + treeRes);

            JsonNode treeNode = objectMapper.readTree(treeRes).get("tree");

            List<String> filePaths = new ArrayList<>();
            for (JsonNode node : treeNode) {
                String path = node.get("path").asText();
                if (extentions.stream().anyMatch(path::endsWith)) {
                    filePaths.add(path);
                    if (filePaths.size() >= 50) break; // 조건에 맞는 파일 최대 50개. 추후 조정
                }
            }
//            log.info("filePaths = " + filePaths);

            // 중요도 점수 계산 및 정렬
            List<FileScore> scoredFiles = new ArrayList<>();
            for (String path : filePaths) {
                int score = calculateFileScore(path);
                scoredFiles.add(new FileScore(path, score));
            }

            // 중요도 점수 기준으로 내림차순 정렬
            scoredFiles.sort(Comparator.comparingInt(FileScore::score).reversed());

            // 중요도 기준 상위 10개 파일 내용 가져오기
            List<String> results = new ArrayList<>();
            for (int i = 0; i < Math.min(10, scoredFiles.size()); i++) {
                String path = scoredFiles.get(i).path();
                String fileRes = createWebClient().get()
                        .uri("/repos/{owner}/{repo}/contents/{path}"
                                , repoInfo.owner(), repoInfo.repo(), path)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                String content = objectMapper.readTree(fileRes).get("content").asText();
                String decoded = new String(Base64.getMimeDecoder().decode(content));
//                    log.info("decoded = " + decoded);
                results.add("[FILE: " + path + "]\n" + decoded);
            }

            return results;
        } catch (Exception e) {
            log.severe("Error fetching important files: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 중요도 점수 계산
    private int calculateFileScore(String path) {
        int score = 0;

        // 중요 파일 이름 기준으로 점수 부여
        if (path.contains("Main") || path.contains("App") || path.contains("Application")) {
            score += 10;
        }
        if (path.contains("Controller") || path.contains("Router") || path.contains("Handler")) {
            score += 8;
        }
        if (path.contains("Service") || path.contains("Manager") || path.contains("UseCase")) {
            score += 7;
        }
        if (path.contains("Config") || path.contains(".config") || path.contains("Settings")) {
            score += 5;
        }
        if (path.contains("Test") || path.contains("Spec")) {
            score -= 2;
        }

        return score;
    }

    // 언어 기반 주요 확장자 결정
    private List<String> decideExtentions(Map<String, Integer> languages) {
        if (languages.isEmpty()) return List.of("java", "py", "js"); // 기본값

        String mainLang = languages.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("java");
        log.info("mainLang = " + mainLang);

        return switch (mainLang.toLowerCase()) {
            case "java" -> List.of("java");
            case "python" -> List.of("py", "ipynb"); // Jupyter Notebook 포함
            case "javascript" -> List.of("js", "jsx", "mjs", "cjs");
            case "typescript" -> List.of("ts", "tsx");
            case "html" -> List.of("html", "htm");
            case "css" -> List.of("css", "scss", "less");
            case "php" -> List.of("php");
            case "c++" -> List.of("cpp", "cc", "cxx", "h", "hpp");
            case "c#" -> List.of("cs");
            case "go" -> List.of("go");
            case "ruby" -> List.of("rb");
            case "rust" -> List.of("rs");
            case "kotlin" -> List.of("kt", "kts");
            case "swift" -> List.of("swift");
            case "objective-c" -> List.of("m", "h");
            case "shell" -> List.of("sh", "bash");
            case "powershell" -> List.of("ps1");
            case "r" -> List.of("r");
            default -> List.of("java", "js", "py");
        };
    }

    // 언어 통계 추출
    private Map<String, Integer> fetchLanguages(GitHubRepoInfo repoInfo) {
        try {
            String response = createWebClient().get()
                    .uri("/repos/{owner}/{repo}/languages",
                            repoInfo.owner(), repoInfo.repo())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("lang_res = " + response);
            return objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    // README 파일 추출
    private String fetchReadme(GitHubRepoInfo repoInfo) {
        try {
            String response = createWebClient().get()
                    .uri("/repos/{owner}/{repo}/readme",
                            repoInfo.owner(), repoInfo.repo())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode node = objectMapper.readTree(response);
//            log.info("node = " + node.toString());
            String content = node.get("content").asText();
//            log.info("content = " + content);
            return new String(Base64.getMimeDecoder().decode(content));
        } catch (Exception e) {
            return null; // README.md 없을 수도 있음
        }
    }

    // github.com/{owner}/{repo} 파싱
    private GitHubRepoInfo parseGitHubUrl(String url) {
        try {
            URI uri = new URI(url);
//            log.info("uri = " + uri.getPath());
            String[] parts = uri.getPath().split("/");
            return new GitHubRepoInfo(parts[1], parts[2]);

        } catch (Exception e) {
            throw new GitHubManagementException("유효하지 않은 GitHub URL입니다.");
        }
    }

    private record FileScore(
            String path,
            int score
    ) {
    }
}
