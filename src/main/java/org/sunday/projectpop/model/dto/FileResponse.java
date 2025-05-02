package org.sunday.projectpop.model.dto;

public record FileResponse(
        String filename,
        String fileUrl,
        String fileType
) {
}
