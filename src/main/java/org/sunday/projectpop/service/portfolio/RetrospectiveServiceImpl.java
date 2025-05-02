package org.sunday.projectpop.service.portfolio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.exceptions.RetrospectiveNotFound;
import org.sunday.projectpop.exceptions.UnauthorizedException;
import org.sunday.projectpop.model.dto.PortfolioRetrospectiveRequest;
import org.sunday.projectpop.model.dto.PortfolioRetrospectiveResponse;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioRetrospective;
import org.sunday.projectpop.model.repository.PortfolioRepository;
import org.sunday.projectpop.model.repository.PortfolioRetrospectiveRepository;

@Service
@RequiredArgsConstructor
public class RetrospectiveServiceImpl implements RetrospectiveService {

    private final PortfolioRetrospectiveRepository retrospectiveRepository;
    private final PortfolioRepository portfolioRepository;

    @Override
    public void addRetrospective(String portfolioId, String userId, PortfolioRetrospectiveRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));
        if (!portfolio.getUserId().equals(userId)) {
            throw new UnauthorizedException("작성 권한이 없습니다.");
        }

        PortfolioRetrospective retrospective = new PortfolioRetrospective();
        retrospective.setPortfolioId(portfolioId);
        retrospective.setUserId(portfolio.getUserId());
        retrospective.setContent(request.content());
        retrospectiveRepository.save(retrospective);
    }

    @Override
    public PortfolioRetrospectiveResponse getRetrospective(String portfolioId, String retrospectiveId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));
        PortfolioRetrospective retrospective = retrospectiveRepository.findById(Long.valueOf(retrospectiveId)).orElseThrow(
                () -> new RetrospectiveNotFound("해당 회고를 찾을 수 없습니다."));
        if (!retrospective.getPortfolioId().equals(portfolioId)) {
            throw new UnauthorizedException("해당 회고는 이 포트폴리오에 속하지 않습니다.");
        }

        return new PortfolioRetrospectiveResponse(
                retrospective.getId(),
                retrospective.getPortfolioId(),
                retrospective.getContent(),
                retrospective.getCreatedAt().toString()
        );
    }
}
