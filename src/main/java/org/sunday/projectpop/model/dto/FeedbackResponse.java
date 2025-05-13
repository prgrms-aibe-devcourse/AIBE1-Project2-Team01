package org.sunday.projectpop.model.dto;

import org.sunday.projectpop.model.enums.AnalysisStatus;

public record FeedbackResponse(
        Long id,
        AnalysisStatus feedbackStatus,
        String llmFeedback
) {
}
