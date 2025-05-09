package org.sunday.projectpop.service.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.*;
import org.sunday.projectpop.model.enums.AnalysisStatus;
import org.sunday.projectpop.model.repository.PortfolioAnalysisRepository;
import org.sunday.projectpop.model.repository.PortfolioFileRepository;
import org.sunday.projectpop.model.repository.PortfolioSummaryRepository;
import org.sunday.projectpop.service.llm.LLMSummaryService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class AnalysisService {

    public static final int MAX_SAFE_LENGTH = 5000;

    private final PortfolioAnalysisRepository analysisRepository;
    private final PortfolioSummaryRepository summaryRepository;
    private final GitHubService gitHubService;
    private final FileReadService fileReadService;
    private final PortfolioFileRepository portfolioFileRepository;
    private final LLMSummaryService llmSummaryService;

    public void handleAnalysis(Portfolio portfolio) {
        PortfolioSummary portfolioSummary = summaryRepository.findByPortfolio(portfolio);

        // 깃허브 및 파일 추출
        List<String> githubText = extractGithubTexts(portfolio.getUrls());
        List<String> fileText = extractFileTexts(portfolio);
        log.info("githubText = " + githubText.toString());
        log.info("fileText = " + fileText.toString());
        if (githubText.isEmpty() && fileText.isEmpty()) {
            portfolioSummary.setStatus(AnalysisStatus.NOT_STARTED);
            summaryRepository.save(portfolioSummary);
            return;
        }

        // 요약 시작
        portfolioSummary.setStatus(AnalysisStatus.GITHUB_IN_PROCESSING);
        summaryRepository.save(portfolioSummary);

        // LLM 비동기 요약 호출
        Mono<String> githubSummaryMono = llmSummaryService.summarizeGithubText(githubText)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(summary -> {
                    log.info("gitHubSummary = " + summary);
                    portfolioSummary.setGithubSummary(summary);
                    portfolioSummary.setStatus(AnalysisStatus.FILE_IN_PROCESSING);
                    summaryRepository.save(portfolioSummary);
                });
        Mono<String> fileSummaryMono = llmSummaryService.summarizeFileText(fileText)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(summary -> {
                    log.info("fileSummary = " + summary);
                    portfolioSummary.setFileSummary(summary);
                    portfolioSummary.setStatus(AnalysisStatus.COMBINED_IN_PROCESSING);
                    summaryRepository.save(portfolioSummary);
                });

        // 두 요약 결과 병합
        Mono.zip(githubSummaryMono, fileSummaryMono)
                .flatMap(tuple -> {
                    String combined = tuple.getT1() + "\n" + tuple.getT2();
                    // 다시 요약 필요하면 비동기 요약 호출
                    return (combined.length() > MAX_SAFE_LENGTH)
                            ? llmSummaryService.summarizeCombinedText(combined)
                            : Mono.just(combined);
                })
                .doOnSuccess(finalSummary -> {
                    log.info("finalSummary = " + finalSummary);
                    portfolioSummary.setFinalSummary(finalSummary);
                    portfolioSummary.setStatus(AnalysisStatus.COMPLETED);
                    summaryRepository.save(portfolioSummary);

                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        success -> log.info("요약 완료"),
                        error -> {
                            log.warning("요약 중 오류 발생 : " + error.getMessage());
                            portfolioSummary.setStatus(AnalysisStatus.FAILED);
                            summaryRepository.save(portfolioSummary);
                        }
                );
    }

    private List<String> extractFileTexts(Portfolio portfolio) {
        // 파일이 있는지 확인
        List<PortfolioFile> portfolioFiles = portfolioFileRepository.findAllByPortfolio(portfolio);
        if (portfolioFiles.isEmpty()) {
            return Collections.emptyList();
        }
        List<ReadableFile> allFiles = new ArrayList<>(portfolioFiles);

        // 파일에서 텍스트 추출
        return fileReadService.extractTextsFromFiles(allFiles);
    }

    private List<String> extractGithubTexts(List<PortfolioUrl> urls) {
        // urls가 바이었다면 바로 빈 리스트 반환
        if (urls == null || urls.isEmpty()) {
            return Collections.emptyList();
        }
        // github.com이 포함된 URL 찾기
        for (PortfolioUrl url : urls) {
            String link = url.getUrl();
            if (link != null && link.contains("github.com")) {
                return gitHubService.fetchAndConvertFiles(link);
                // 현재는 하나의 링크만 처리
            }
        }
        // 없다면 빈 리스트 반환
        return Collections.emptyList();
    }
}
