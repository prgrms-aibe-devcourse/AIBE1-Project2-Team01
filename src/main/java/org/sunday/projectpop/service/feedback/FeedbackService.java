package org.sunday.projectpop.service.feedback;

import org.sunday.projectpop.model.dto.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    void generatePortfolioFeedback(String id);

    List<FeedbackResponse> getFeedbackList(String portfolioId);
}
