package org.sunday.projectpop.model.dto;

import java.time.LocalDateTime;

public record SuggestionDto(
        Long id,
        String message,
        String projectId,
        String senderId,
        String receiverId,
        LocalDateTime createdAt
) {}
