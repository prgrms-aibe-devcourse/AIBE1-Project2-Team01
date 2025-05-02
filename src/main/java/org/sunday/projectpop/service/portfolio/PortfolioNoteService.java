package org.sunday.projectpop.service.portfolio;

import org.sunday.projectpop.model.dto.PortfolioNoteRequest;
import org.sunday.projectpop.model.dto.PortfolioNoteResponse;

public interface PortfolioNoteService {
    void createNote(String portfolioId, String userId, PortfolioNoteRequest request);

    PortfolioNoteResponse getNote(String portfolioId, String noteId);
}
