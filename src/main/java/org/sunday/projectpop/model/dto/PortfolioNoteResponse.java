package org.sunday.projectpop.model.dto;

public record PortfolioNoteResponse(
        Long id,
        String content,
        String createdAt,
        boolean hasFile
) {
}
