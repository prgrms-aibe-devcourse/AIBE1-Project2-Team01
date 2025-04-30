package org.sunday.projectpop.service.portfolio;

import org.sunday.projectpop.model.dto.PortfolioCreateRequest;

public interface PortfolioService {
    void createPortfolio(String userId, PortfolioCreateRequest request);
}
