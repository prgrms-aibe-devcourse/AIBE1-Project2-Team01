package org.sunday.projectpop.model.dto;

import org.sunday.projectpop.model.enums.PortfoliosType;

import java.util.List;

public record PortfolioUpdateRequest(
        PortfoliosType portfolioType,
        String title,
        String description,

//        List<MultipartFile> newFiles,
        List<String> newUrls,

        List<Long> deleteFileIds,
        List<Long> deleteUrlIds
) {
}
