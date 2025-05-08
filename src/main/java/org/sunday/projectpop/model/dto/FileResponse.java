package org.sunday.projectpop.model.dto;

public record FileResponse(
        Long id,
        String filename,
        String fileUrl,
        String fileType
) {
}
