package org.sunday.projectpop.service.feedback;

import org.sunday.projectpop.model.dto.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    FeedbackResponse generatePortfolioFeedback(String id, Long noteId);

    List<FeedbackResponse> getFeedbackList(String portfolioId);

    FeedbackResponse getFeedback(Long feedbackId);

    FeedbackResponse getLatestFeedback(String portfolioId, Long noteId);
}
