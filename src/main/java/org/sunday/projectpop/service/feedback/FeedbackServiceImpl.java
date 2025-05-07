package org.sunday.projectpop.service.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.repository.PortfolioFileRepository;
import org.sunday.projectpop.model.repository.PortfolioNoteFileRepository;
import org.sunday.projectpop.model.repository.PortfolioRepository;

@Service
@RequiredArgsConstructor
@Log
public class FeedbackServiceImpl implements FeedbackService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioFileRepository portfolioFileRepository;
    private final PortfolioNoteFileRepository portfolioNoteFileRepository;
    private final GitHubService gitHubService;
    private final FileReadService fileReadService;

    @Override
    public void generateFeedback(String id) {
        // 포트폴리오 있는지 확인
        Portfolio portfolio = findById(id);

        // TODO: LLM에 전달
        // TODO: DB 저장 (feedback)
    }

    private Portfolio findById(String id) {
        return portfolioRepository.findById(id).orElseThrow(
                () -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다.")
        );
    }
}
