package org.sunday.projectpop.service.portfolio;

import org.sunday.projectpop.model.dto.PortfolioRetrospectiveRequest;

public interface RetrospectiveService {
    void addRetrospective(String portfolioId, String userId, PortfolioRetrospectiveRequest request);
}
