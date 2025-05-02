package org.sunday.projectpop.service.portfolio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.exceptions.PortfolioNoteNotFound;
import org.sunday.projectpop.exceptions.UnauthorizedException;
import org.sunday.projectpop.model.dto.PortfolioNoteRequest;
import org.sunday.projectpop.model.dto.PortfolioNoteResponse;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioNote;
import org.sunday.projectpop.model.repository.PortfolioRepository;
import org.sunday.projectpop.model.repository.PortfolioNoteRepository;

@Service
@RequiredArgsConstructor
public class PortfolioNoteServiceImpl implements PortfolioNoteService {

    private final PortfolioNoteRepository noteRepository;
    private final PortfolioRepository portfolioRepository;

    @Override
    public void createNote(String portfolioId, String userId, PortfolioNoteRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));
        if (!portfolio.getUserId().equals(userId)) {
            throw new UnauthorizedException("작성 권한이 없습니다.");
        }

        PortfolioNote note = new PortfolioNote();
        note.setPortfolio(portfolio);
        note.setUserId(portfolio.getUserId());
        note.setContent(request.content());
        noteRepository.save(note);
    }

    @Override
    public PortfolioNoteResponse getNote(String portfolioId, String retrospectiveId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));
        PortfolioNote note = noteRepository.findById(Long.valueOf(retrospectiveId)).orElseThrow(
                () -> new PortfolioNoteNotFound("해당 회고를 찾을 수 없습니다."));
//        if (!note.getPortfolioId().equals(portfolioId)) {
//            throw new UnauthorizedException("해당 회고는 이 포트폴리오에 속하지 않습니다.");
//        }

        return null;
//        return new PortfolioNoteResponse(
//                note.getId(),
//                note.getPortfolio(),
//                note.getContent(),
//                note.getCreatedAt().toString()
//        );
    }
}
