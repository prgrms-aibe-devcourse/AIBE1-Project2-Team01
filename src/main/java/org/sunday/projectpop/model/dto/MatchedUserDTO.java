package org.sunday.projectpop.model.dto;

public record MatchedUserDTO(
        String userId,
        String email,
        int compatibilityScore
) {}
