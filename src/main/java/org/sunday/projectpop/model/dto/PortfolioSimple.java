package org.sunday.projectpop.model.dto;

import org.sunday.projectpop.model.enums.PortfolioType;

public record PortfolioSimple(
        String portfolioId,
        PortfolioType portfolioType,
        String title,
        String createdAt
) {
}
