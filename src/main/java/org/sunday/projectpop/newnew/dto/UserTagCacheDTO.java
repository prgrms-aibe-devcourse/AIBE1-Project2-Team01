package org.sunday.projectpop.newnew.dto;

import java.util.List;
import java.util.UUID;

public record UserTagCacheDTO(
        UUID userId,
        List<String> techTags,
        Integer personalityScore,     // 1 ~ 5
        String personalityText        // 예: 조용함 ~ 활발함
) {}
