package org.sunday.projectpop.model.dto;

import java.time.LocalDateTime;

public record EnhancedSuggestionDto(
        Long id,
        String message,
        String senderId,
        String receiverId,
        LocalDateTime createdAt,
        ProjectPreview project,
        Boolean checking
) {
    public record ProjectPreview(
            String projectId,
            String title,
            String description
    ) {}

    public static EnhancedSuggestionDto fromEntity(org.sunday.projectpop.model.entity.SuggestFromLeader s) {
        return new EnhancedSuggestionDto(
                s.getId(),
                s.getMessage(),
                s.getSender().getUserId(),
                s.getReceiver().getUserId(),
                s.getCreatedAt(),
                new ProjectPreview(
                        s.getProject().getProjectId(),
                        s.getProject().getTitle(),
                        s.getProject().getDescription()
                ),
                s.isChecking()
        );
    }
}
