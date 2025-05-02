package org.sunday.projectpop.model.dto;

import org.sunday.projectpop.model.enums.PortfoliosType;

import java.util.List;

public record PortfolioResponse(
        String portfolioId,
        PortfoliosType portfolioType,
        String title,
        String description,
        String createdAt,

        List<String> urls,
        List<FileResponse> files

) {
}
