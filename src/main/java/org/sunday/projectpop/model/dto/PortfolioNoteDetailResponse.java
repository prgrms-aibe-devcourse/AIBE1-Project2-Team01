package org.sunday.projectpop.model.dto;

import java.util.List;

public record PortfolioNoteDetailResponse(
        Long id,
        String content,
        String createdAt,
        List<FileResponse> files
) {
}
