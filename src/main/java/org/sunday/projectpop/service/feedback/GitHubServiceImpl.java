package org.sunday.projectpop.service.feedback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.sunday.projectpop.exceptions.GitHubManagementException;
import org.sunday.projectpop.model.dto.GitHubRepoInfo;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;
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

        List<String> codeFiles = fetchImportantFiles(repoInfo, targetExtentions, mainLang(languages));

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
    private List<String> fetchImportantFiles(GitHubRepoInfo repoInfo, List<String> extentions, String mainLang) {
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
                    if (filePaths.size() >= 100) break; // 조건에 맞는 파일 최대 100개. 추후 조정
                }
            }
//            log.info("filePaths = " + filePaths);

            // 중요도 점수 계산 및 정렬
            List<FileScore> scoredFiles = new ArrayList<>();
            for (String path : filePaths) {
                int score = calculateFileScore(path, mainLang);
//                log.info("path: %s, score: %d".formatted(path, score));
                scoredFiles.add(new FileScore(path, score));
            }

            // 중요도 점수 기준으로 내림차순 정렬
            scoredFiles.sort(Comparator.comparingInt(FileScore::score).reversed());

            // 중요도 기준 상위 10개 파일 내용 가져오기
            List<String> results = new ArrayList<>();
            for (int i = 0; i < Math.min(10, scoredFiles.size()); i++) {
                String path = scoredFiles.get(i).path();
                String content = fetchFileContent(repoInfo, path);
                results.add("[FILE: " + path + "]\n" + content);
            }

            return results;
        } catch (Exception e) {
            log.severe("Error fetching important files: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    private String fetchFileContent(GitHubRepoInfo repoInfo, String path) throws JsonProcessingException {
        String fileRes = createWebClient().get()
                .uri("/repos/{owner}/{repo}/contents/{path}"
                        , repoInfo.owner(), repoInfo.repo(), path)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        String content = objectMapper.readTree(fileRes).get("content").asText();
        return new String(Base64.getMimeDecoder().decode(content));
    }

    // 중요도 점수 계산
    private int calculateFileScore(String path, String extension) {
        path = path.toLowerCase();

        return switch (extension) {
            case "java" -> calculateScoreForJava(path);
            case "py" -> calculateScoreForPython(path);
            case "javascript", "html" -> calculateScoreForJsHtml(path);
            default -> 0;
        };
    }

    private int calculateScoreForJava(String path) {
        int score = 0;

        if (!path.endsWith(".java")) return 0;

        if (path.contains("controller") || path.contains("service") || path.contains("repository")) score += 3;
        if (path.contains("model") || path.contains("domain") || path.contains("entity")) score += 2;

        if (path.endsWith("controller.java")) score += 4;
        if (path.endsWith("application.java")) score += 3;

        return score;
    }

    private int calculateScoreForPython(String path) {
        int score = 0;

        if (!path.endsWith(".py")) return 0;

        if (path.contains("views") || path.contains("routes") || path.contains("models")) score += 3;
        if (path.endsWith("main.py") || path.endsWith("app.py")) score += 4;
        if (path.endsWith("urls.py") || path.endsWith("utils.py")) score += 2;

        return score;
    }

    private int calculateScoreForJsHtml(String path) {
        int score = 0;

        if (path.endsWith(".js") || path.endsWith(".html") || path.endsWith("htm")) score += 1;

        if (path.contains("account") || path.contains("auth") || path.contains("component")) score += 2;
        if (path.contains("app.js") || path.contains("index.html") || path.contains("router.js")) score += 3;

        return score;
    }

    // 주언어
    private String mainLang(Map<String, Integer> languages) {
        return languages.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("java")
                .toLowerCase();
    }

    // 언어 기반 주요 확장자 결정
    private List<String> decideExtentions(Map<String, Integer> languages) {
        if (languages.isEmpty()) return List.of("java", "py", "js"); // 기본값

        return switch (mainLang(languages)) {
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


    // 마지막 커밋 시간 리턴
    public Instant fetchUpdatedAtFromGithub(String githubUrl) {
        GitHubRepoInfo repoInfo = parseGitHubUrl(githubUrl);

        return createWebClient().get()
                .uri("/repos/{owner}/{repo}", repoInfo.owner(), repoInfo.repo())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
//                    log.info("githubPushed: " + response.toString());
                    String updatedAt = (String) response.get("updated_at");
//                    log.info("updatedAt: " + updatedAt);
                    return Instant.parse(updatedAt);
                }).block();
    }

    private record FileScore(
            String path,
            int score
    ) {
    }
}
