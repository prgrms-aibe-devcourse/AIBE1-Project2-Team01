package org.sunday.projectpop.model.dto;

import org.sunday.projectpop.model.enums.PortfoliosType;

public record PortfolioResponse(
        String portfolioId,
        PortfoliosType portfolioType,
        String url,
        String description,
        String createdAt
) {
}
