package org.sunday.projectpop.model.dto;

import java.util.List;

public record PortfolioNoteUpdateRequest(
        String content,
        List<Long> deleteFileIds
) {
}
