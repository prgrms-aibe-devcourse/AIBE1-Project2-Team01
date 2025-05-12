package org.sunday.projectpop.service.portfolio;

import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.model.dto.PortfolioRequest;
import org.sunday.projectpop.model.dto.PortfolioResponse;
import org.sunday.projectpop.model.dto.PortfolioSimple;

import java.util.List;

public interface PortfolioService {
    void createPortfolio(String userId, PortfolioRequest request);

    List<PortfolioSimple> getMyPortfolios(String userId) throws PortfolioNotFoundException;

    PortfolioResponse getPortfolio(String portfolioId);

    void updatePortfolio(String userId, String portfolioId, PortfolioRequest request);

    void deletePortfolio(String userId, String portfolioId);

}
