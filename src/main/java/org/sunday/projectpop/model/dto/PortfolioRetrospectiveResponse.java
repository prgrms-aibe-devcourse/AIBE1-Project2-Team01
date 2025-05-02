package org.sunday.projectpop.model.dto;

public record PortfolioRetrospectiveResponse(
        Long id,
        String portfolioId,
//        String userId,
        String content,
        String createdAt
) {
}
