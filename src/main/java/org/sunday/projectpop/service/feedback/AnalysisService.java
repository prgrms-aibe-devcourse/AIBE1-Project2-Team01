package org.sunday.projectpop.service.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunday.projectpop.model.entity.*;
import org.sunday.projectpop.model.enums.AnalysisStatus;
import org.sunday.projectpop.model.repository.PortfolioFileRepository;
import org.sunday.projectpop.model.repository.PortfolioSummaryRepository;
import org.sunday.projectpop.service.llm.LLMSummaryService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class AnalysisService {

    public static final int MAX_SAFE_LENGTH = 5000;

    private final PortfolioSummaryRepository summaryRepository;
    private final GitHubService gitHubService;
    private final FileReadService fileReadService;
    private final PortfolioFileRepository portfolioFileRepository;
    private final LLMSummaryService llmSummaryService;

    /**
     * 포트폴리오 분석 처리 (쓰기 작업)
     * - 트랜잭션 범위: DB 상태 변경까지 포함
     * - rollbackFor: 모든 예외 발생 시 롤백
     * - Reactor 스케줄러와의 충돌 방지를 위해 트랜잭션 분리
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleAnalysis(Portfolio portfolio) {
        PortfolioSummary portfolioSummary = initAnalysisProcess(portfolio);
        AnalysisData data = extractAnalysisData(portfolio);

        if (data.isEmpty()) {
            abortAnalysis(portfolioSummary);
            return;
        }
        // 요약 시작
        startAsyncAnalysis(portfolioSummary, data);
    }

    /**
     * 노트 제출 처리 (쓰기 작업)
     * - 트랜잭션 범위: DB 상태 변경까지 포함
     * - GitHub 갱신 여부 확인 후 분석 재시작
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleNoteSubmit(Portfolio portfolio) {
        // 깃허브 링크 유무 확인
        List<PortfolioUrl> urls = portfolio.getUrls();
        PortfolioSummary summary = summaryRepository.findByPortfolio(portfolio);

        urls.stream()
                .filter(url -> url.getUrl() != null && url.getUrl().contains("github.com"))
                .findFirst()
                .map(url -> gitHubService.isAfterLastCommit(url.getUrl(), summary.getCreatedAt().toInstant()))
                .ifPresent(isAfterLastCommit -> {
                    if (isAfterLastCommit) {
                        handleAnalysis(portfolio);
                    }
                });
    }

    /**
     * 비동기 분석 시작
     * - 트랜잭션 외부에서 실행 (Reactor 스케줄러와의 충돌 방지)
     * - 각 단계별 상태 업데이트는 별도 트랜잭션으로 처리
     */
    /*
        private void startAsyncAnalysis(PortfolioSummary summary, AnalysisData data) {
        Mono.zip(
                        processGithubData(summary, data.githubTexts),
                        processFileData(summary, data.fileTexts)
                )
                .flatMap(tuple -> generateFinalSummary(tuple.getT1(), tuple.getT2()))
                .doOnSuccess(finalSummary -> completeAnalysis(summary, finalSummary))
                .doOnError(error -> failAnalysis(summary, error))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
     */
    private void startAsyncAnalysis(PortfolioSummary summary, AnalysisData data) {
        extractGithubTexts(data.githubUrls)
                .flatMap(githubTexts ->
                        Mono.zip(
                                processGithubData(summary, githubTexts),
                                processFileData(summary, data.fileTexts)
                        )
                )
                .flatMap(tuple -> generateFinalSummary(tuple.getT1(), tuple.getT2()))
                .doOnSuccess(finalSummary -> completeAnalysis(summary, finalSummary))
                .doOnError(error -> failAnalysis(summary, error))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    /**
     * 최종 요약 생성
     */
    private Mono<String> generateFinalSummary(String githubSummary, String fileSummary) {
        String combined = githubSummary + "\n" + fileSummary;
        return (combined.length() > MAX_SAFE_LENGTH)
                ? llmSummaryService.summarizeCombinedText(combined)
                : Mono.just(combined);
    }

    /**
     * 분석 완료 처리
     */
    private void completeAnalysis(PortfolioSummary summary, String finalSummary) {
        updateAnalysisStatus(
                summary.getId(),
                AnalysisStatus.COMPLETED,
                s -> s.setFinalSummary(finalSummary)
        );
        log.info("포트폴리오 분석 완료: " + summary.getId());
    }

    /**
     * 분석 실패 처리
     */
    private void failAnalysis(PortfolioSummary summary, Throwable error) {
        log.warning("분석 실패: " + error.getMessage());
        updateAnalysisStatus(
                summary.getId(),
                AnalysisStatus.FAILED,
                s -> {
                }
        );
    }

    /**
     * 상태 업데이트 공통 처리 (별도 트랜잭션)
     */
    @Transactional
    protected void updateAnalysisStatus(Long summaryId, AnalysisStatus status, java.util.function.Consumer<PortfolioSummary> updater) {
        int maxAttempts = 3;
        int attempt = 0;
        boolean success = false;

        while (attempt < maxAttempts && !success) {
            try {
                summaryRepository.findById(summaryId).ifPresent(summary -> {
                    summary.setStatus(status);
                    updater.accept(summary);
                    summaryRepository.save(summary);
                });
                success = true;
            } catch (ObjectOptimisticLockingFailureException e) {
                attempt++;
                if (attempt >= maxAttempts) {
                    throw e; // 최대 시도 횟수 초과 시 예외 전파
                }
                try {
                    Thread.sleep(100L * attempt); // 점진적 대기
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 중단", ie);
                }
            }
        }
    }

    /**
     * 파일 데이터 요약
     */
    private Mono<String> processFileData(PortfolioSummary portfolioSummary, List<String> texts) {
        return llmSummaryService.summarizeFileText(texts)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(summary -> {
                    log.info("fileSummary = " + summary);
                    portfolioSummary.setFileSummary(summary);
                    portfolioSummary.setStatus(AnalysisStatus.COMBINED_IN_PROCESSING);
                    summaryRepository.save(portfolioSummary);
                });
    }

    /**
     * GitHub 데이터 요약
     */
    private Mono<String> processGithubData(PortfolioSummary portfolioSummary, List<String> texts) {
        return llmSummaryService.summarizeGithubText(texts)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(summary -> {
                    log.info("gitHubSummary = " + summary);
                    portfolioSummary.setGithubSummary(summary);
                    portfolioSummary.setStatus(AnalysisStatus.FILE_IN_PROCESSING);
                    summaryRepository.save(portfolioSummary);
                });
    }

    /**
     * 분석 중단 처리
     */
    private void abortAnalysis(PortfolioSummary summary) {
        summary.setStatus(AnalysisStatus.NOT_STARTED);
        summaryRepository.save(summary);
    }

    /**
     * 분석 데이터 추출 (읽기 전용)
     */
    private AnalysisData extractAnalysisData(Portfolio portfolio) {
        return new AnalysisData(
                portfolio.getUrls(),
                extractFileTexts(portfolio)
        );
    }

    /**
     * 요약 프로세스 초기화
     */
    private PortfolioSummary initAnalysisProcess(Portfolio portfolio) {
        PortfolioSummary summary = portfolio.getSummary();
        summary.setStatus(AnalysisStatus.GITHUB_IN_PROCESSING);

        if (summary.getCreatedAt() == null) {
            summary.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC));
        }
        return summaryRepository.save(summary);
    }

    /**
     * 분석 재시작 여부 확인
     */
    private boolean shouldRestartAnalysis(PortfolioSummary summary, Instant lastUpdate) {
        return summary != null
                && summary.getStatus() == AnalysisStatus.COMPLETED
                && lastUpdate.isAfter(summary.getCreatedAt().toInstant());
    }


    /**
     * 파일 텍스트 추출 (읽기 전용)
     */
    @Transactional(readOnly = true)
    protected List<String> extractFileTexts(Portfolio portfolio) {
        // 파일이 있는지 확인
        List<PortfolioFile> portfolioFiles = portfolioFileRepository.findAllByPortfolio(portfolio);
        if (portfolioFiles.isEmpty()) {
            return Collections.emptyList();
        }
        List<ReadableFile> allFiles = new ArrayList<>(portfolioFiles);
        return fileReadService.extractTextsFromFiles(allFiles);
    }

    /**
     * GitHub 텍스트 추출 (읽기 전용)
     */
    @Transactional(readOnly = true)
    protected Mono<List<String>> extractGithubTexts(List<PortfolioUrl> urls) {
        if (urls == null || urls.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }
        return urls.stream()
                .filter(url -> url.getUrl() != null && url.getUrl().contains("github.com"))
                .findFirst()
                .map(url -> gitHubService.summarizeProject(url.getUrl())
                        .map(dto -> {
                            List<String> result = new ArrayList<>();
                            result.add("[디렉터리 구조] %s".formatted(dto.getDirectoryTree()));
                            result.add("[언어 구조] %s".formatted(dto.getLanguages()));
                            result.add("[코드 구조] %s".formatted(dto.getCodeStructure()));
                            result.add("[README 요약] %s".formatted(dto.getReadmeSummary()));
                            result.add("[최근 커밋] %s".formatted(dto.getCommitSummary()));
                            result.add("[CI/CD] %s".formatted(dto.getCiCd()));
                            log.info("result = " + result);
                            return result;
                        }))
                .orElse(Mono.just(Collections.emptyList()));
    }

    private record AnalysisData(List<PortfolioUrl> githubUrls, List<String> fileTexts) {
        boolean isEmpty() {
            return (githubUrls == null || githubUrls.isEmpty()) && fileTexts.isEmpty();
        }
    }
}
