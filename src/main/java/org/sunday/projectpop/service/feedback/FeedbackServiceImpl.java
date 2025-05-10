package org.sunday.projectpop.service.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(rollbackFor = Exception.class)  // 모든 예외 발생 시 롤백
    public FeedbackResponse generatePortfolioFeedback(String portfolioId, Long noteId) {
        // 포트폴리오 있는지 확인
        Portfolio portfolio = findById(portfolioId);
        PortfolioNote note = portfolio.getNotes().stream()
                .filter(n -> n.getId().equals(noteId))
                .findFirst()
                .orElseThrow(() -> new PortfolioNotFoundException("해당 노트를 찾을 수 없습니다."));
        log.info("note = " + note.getId());

        PortfolioSummary summary = summaryRepository.findByPortfolio(portfolio);

        if (!summary.getStatus().equals(AnalysisStatus.COMPLETED)) {
            log.info("요약이 완료되지 않았습니다.");
            return null;
        }
        String finalSummary = summary.getFinalSummary();
        PortfolioFeedback prevFeedback = portfolioFeedbackRepository.findTopByPortfolioOrderByCreatedAtDesc(portfolio);

        // 추가 데이터 (포트폴리오설명 + 노트내용)
        String description = portfolio.getDescription();
        String noteContent = note.getContent();
        String data = """
                    [포트폴리오 설명] %s
                    [노트 내용] %s
                    [포트폴리오 요약] %s
                    """.formatted(description, noteContent, finalSummary);
        boolean isFirst = true;

        // 이전 피드백 유무에 따라 분기
        if (prevFeedback != null) {
            String prev = prevFeedback.getLlmFeedback();
            log.info("prev = " + prev);
            data += """
                    [이전 피드백] %s
                    """.formatted(prev);
            isFirst = false;
        }

        log.info("data = " + data);

        PortfolioFeedback feedback = generateFeedbackLLM(portfolio, note, data, isFirst);
        return getFeedback(feedback.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackList(String portfolioId) {
        Portfolio portfolio = findById(portfolioId);
        List<FeedbackResponse> feedbackResponses = portfolioFeedbackRepository.findFeedbackAndStatusByPortfolio(portfolio);
        if (feedbackResponses.stream().anyMatch(feedbackResponse -> feedbackResponse.feedbackStatus() == null)) {
            throw new FeedbackNotFoundException("등록된 피드백이 없습니다.");
        }
        return feedbackResponses;
    }

    @Override
    @Transactional(readOnly = true)  // 읽기 전용 트랜잭션
    public FeedbackResponse getFeedback(Long feedbackId) {
        return portfolioFeedbackRepository.findFeedbackAndStatusById(feedbackId);
    }

    @Override
    @Transactional(readOnly = true)  // 읽기 전용 트랜잭션
    public FeedbackResponse getLatestFeedback(String portfolioId, Long noteId) {
        Portfolio portfolio = findById(portfolioId);
        PortfolioNote note = findNote(noteId);
        return portfolioFeedbackRepository.findLatestFeedback(portfolio, note);
    }

    @Transactional
    protected PortfolioFeedback generateFeedbackLLM(Portfolio portfolio, PortfolioNote note, String data, boolean isFirst) {
        PortfolioFeedback feedback = new PortfolioFeedback();
        feedback.setPortfolio(portfolio);
        feedback.setNote(note);
        feedback.setStatus(AnalysisStatus.FEEDBACK_IN_PROCESSING);
        portfolioFeedbackRepository.save(feedback); // 상태 업데이트
        String type = isFirst ? "first_develop" : "develop";
        llmClient.feedback(data, type)
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
        return feedback;
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
