package org.sunday.projectpop.project.model.dto;

import java.util.List;

public record GeminiResponse(
        String title,
        String description,
        int teamSize,
        List<String> requiredTags,
        List<String> selectiveTags,
        String field,
        // String locationType,
        int durationWeeks
) {}