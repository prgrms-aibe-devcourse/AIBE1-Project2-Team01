package org.sunday.projectpop.project.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // 👈 locationType 같이 정의되지 않은 필드 무시
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