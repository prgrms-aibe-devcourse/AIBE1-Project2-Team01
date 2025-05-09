package org.sunday.projectpop.service.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.exceptions.FeedbackNotFoundException;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.model.dto.FeedbackResponse;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioFeedback;
import org.sunday.projectpop.model.entity.PortfolioNote;
import org.sunday.projectpop.model.entity.PortfolioSummary;
import org.sunday.projectpop.model.enums.AnalysisStatus;
import org.sunday.projectpop.model.repository.PortfolioFeedbackRepository;
import org.sunday.projectpop.model.repository.PortfolioNoteRepository;
import org.sunday.projectpop.model.repository.PortfolioRepository;
import org.sunday.projectpop.model.repository.PortfolioSummaryRepository;
import org.sunday.projectpop.service.llm.LLMClient;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class FeedbackServiceImpl implements FeedbackService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioSummaryRepository summaryRepository;
    private final LLMClient llmClient;
    private final PortfolioFeedbackRepository portfolioFeedbackRepository;
    private final PortfolioNoteRepository portfolioNoteRepository;

    @Override
    public FeedbackResponse generatePortfolioFeedback(String id, Long noteId) {
        // 포트폴리오 있는지 확인
        Portfolio portfolio = findById(id);
        PortfolioNote note = portfolio.getNotes().stream()
                .filter(n -> n.getId().equals(noteId))
                .findFirst()
                .orElseThrow(() -> new PortfolioNotFoundException("해당 노트를 찾을 수 없습니다."));
        log.info("note = " + note.getId());

        PortfolioSummary summary = summaryRepository.findByPortfolio(portfolio);

        if (!summary.getStatus().equals(AnalysisStatus.COMPLETED)) {
            log.info("요약이 완료되지 않았습니다.");
            // TODO: 어떻게 처리할지 고민
            return null;
        }
        String finalSummary = summary.getFinalSummary();
        PortfolioFeedback prevFeedback = portfolioFeedbackRepository.findTopByPortfolioOrderByCreatedAtDesc(portfolio);

        PortfolioFeedback feedback = new PortfolioFeedback();
        feedback.setPortfolio(portfolio);
        feedback.setNote(note);
        feedback.setStatus(AnalysisStatus.FEEDBACK_IN_PROCESSING);
        portfolioFeedbackRepository.save(feedback); // 상태 업데이트

        // 추가 데이터 (포트폴리오설명 + 노트내용)
        String description = portfolio.getDescription();
        String noteContent = note.getContent();
        String data = "";
        boolean isFirst = true;

        // TODO: 이전 피드백 유무에 따라 분기
        if (prevFeedback != null) {
            String prev = prevFeedback.getLlmFeedback();
            log.info("prev = " + prev);
            data = """
                    [포트폴리오 설명] %s
                    [회고 및 노트 내용] %s
                    [포트폴리오 요약] %s
                    [이전 피드백] %s
                    """.formatted(description, noteContent, finalSummary, prev);
            isFirst = false;
        } else {
            data = """
                    [포트폴리오 설명] %s
                    [회고 및 노트 내용] %s
                    [포트폴리오 요약] %s
                    """.formatted(description, noteContent, finalSummary);
        }

        llmClient.feedback(data, isFirst)
                .doOnNext(result -> {
                    log.info("feedback = " + result);
                    feedback.setLlmFeedback(result);
                    feedback.setStatus(AnalysisStatus.COMPLETED);
                    portfolioFeedbackRepository.save(feedback); // 상태 업데이트
                })
                .doOnError(error -> {
                    log.warning("피드백 생성 중 오류 발생 : " + error.getMessage());
                    feedback.setStatus(AnalysisStatus.FAILED);
                    portfolioFeedbackRepository.save(feedback); // 상태 업데이트
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();

        return getFeedback(feedback.getId());
    }

    @Override
    public List<FeedbackResponse> getFeedbackList(String portfolioId) {
        Portfolio portfolio = findById(portfolioId);
        List<FeedbackResponse> feedbackResponses = portfolioFeedbackRepository.findFeedbackAndStatusByPortfolio(portfolio);
        if (feedbackResponses.stream().anyMatch(feedbackResponse -> feedbackResponse.feedbackStatus() == null)) {
            throw new FeedbackNotFoundException("등록된 피드백이 없습니다.");
        }

        return feedbackResponses;
    }

    @Override
    public FeedbackResponse getFeedback(Long feedbackId) {
        return portfolioFeedbackRepository.findFeedbackAndStatusById(feedbackId);
    }

    @Override
    public FeedbackResponse getLatestFeedback(String portfolioId, Long noteId) {
        Portfolio portfolio = findById(portfolioId);
        PortfolioNote note = findNote(noteId);
        FeedbackResponse latestFeedback = portfolioFeedbackRepository.findLatestFeedback(portfolio, note);
        log.info(latestFeedback.llmFeedback());
        return latestFeedback;
    }

    private Portfolio findById(String id) {
        return portfolioRepository.findById(id).orElseThrow(
                () -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다.")
        );
    }

    private PortfolioNote findNote(Long noteId) {
        return portfolioNoteRepository.findById(noteId).orElseThrow(
                () -> new PortfolioNotFoundException("해당 노트를 찾을 수 없습니다.")
        );
    }
}
