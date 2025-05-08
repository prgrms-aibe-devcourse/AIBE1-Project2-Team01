package org.sunday.projectpop.model.dto;

import org.sunday.projectpop.model.enums.PortfoliosType;

public record PortfolioSimple(
        String portfolioId,
        PortfoliosType portfolioType,
        String title,
        String createdAt
) {
}
