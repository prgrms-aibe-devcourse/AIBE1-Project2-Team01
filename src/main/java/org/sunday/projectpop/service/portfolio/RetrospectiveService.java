package org.sunday.projectpop.service.portfolio;

import org.sunday.projectpop.model.dto.PortfolioRetrospectiveRequest;
import org.sunday.projectpop.model.dto.PortfolioRetrospectiveResponse;

public interface RetrospectiveService {
    void addRetrospective(String portfolioId, String userId, PortfolioRetrospectiveRequest request);

    PortfolioRetrospectiveResponse getRetrospective(String portfolioId, String retrospectiveId);
}
