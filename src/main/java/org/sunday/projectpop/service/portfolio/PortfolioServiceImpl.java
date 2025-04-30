package org.sunday.projectpop.service.portfolio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.PortfolioCreateRequest;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.repository.PortfolioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Override
    public void createPortfolio(String userId, PortfolioCreateRequest request) {
        // TODO: userId 한번더 체크
//        UserAccount userAccount = userAccountRepository.findByUserId(userId).orElseThrow(
//        () -> new Exception("유저를 찾을 수 없습니다."));

        Portfolio portfolio = new Portfolio();
//        portfolio.setUserId(userAccount.getUserId());
        portfolio.setUserId(userId);
        portfolio.setPortfolioType(request.portfolioType());
        portfolio.setUrl(request.url());
        portfolio.setDescription(request.description());
        portfolio.setFromHere(true); // TODO: 이게 필요한지 생각해보기!!
        portfolioRepository.save(portfolio);
    }
}

