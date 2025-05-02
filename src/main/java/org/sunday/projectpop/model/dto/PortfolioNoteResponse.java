package org.sunday.projectpop.model.dto;

public record PortfolioNoteResponse(
        Long id,
        String portfolioId,
//        String userId,
        String content,
        String createdAt
) {
}
