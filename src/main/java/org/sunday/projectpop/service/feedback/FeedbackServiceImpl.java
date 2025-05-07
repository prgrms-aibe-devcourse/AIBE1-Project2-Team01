package org.sunday.projectpop.service.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioAnalysis;
import org.sunday.projectpop.model.enums.AnalysisStatus;
import org.sunday.projectpop.model.repository.PortfolioAnalysisRepository;
import org.sunday.projectpop.model.repository.PortfolioRepository;
import org.sunday.projectpop.service.llm.LLMClient;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Log
public class FeedbackServiceImpl implements FeedbackService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioAnalysisRepository analysisRepository;
    private final LLMClient llmClient;

    @Override
    public void generateFeedback(String id) {
        // 포트폴리오 있는지 확인
        Portfolio portfolio = findById(id);
        PortfolioAnalysis analysis = analysisRepository.findByPortfolio(portfolio);

        if (!analysis.getSummaryStatus().equals(AnalysisStatus.COMPLETED)) {
            log.info("요약이 완료되지 않았습니다.");
            // TODO: 어떻게 처리할지 고민
            return;
        }
        String summary = analysis.getFinalSummary();
        analysis.setFeedbackStatus(AnalysisStatus.FEEDBACK_IN_PROCESSING);
        analysisRepository.save(analysis); // 상태 업데이트

        llmClient.feedback(summary)
                .doOnNext(feedback -> {
                    log.info("feedback = " + feedback);
                    analysis.setLlmFeedback(feedback);
                    analysis.setFeedbackStatus(AnalysisStatus.COMPLETED);
                    analysisRepository.save(analysis); // 상태 업데이트
                })
                .doOnError(error -> {
                    log.warning("피드백 생성 중 오류 발생 : " + error.getMessage());
                    analysis.setFeedbackStatus(AnalysisStatus.FAILED);
                    analysisRepository.save(analysis); // 상태 업데이트
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();

    }

    private Portfolio findById(String id) {
        return portfolioRepository.findById(id).orElseThrow(
                () -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다.")
        );
    }
}
