package org.sunday.projectpop.service.portfolio;

import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.model.dto.PortfolioNoteCreateRequest;
import org.sunday.projectpop.model.dto.PortfolioNoteDetailResponse;
import org.sunday.projectpop.model.dto.PortfolioNoteResponse;

import java.util.List;

public interface PortfolioNoteService {

    void createNote(String userId, String portfolioId, PortfolioNoteCreateRequest request, List<MultipartFile> files);

    List<PortfolioNoteResponse> getPortfolioNoteList(String portfolioId);

    PortfolioNoteDetailResponse getPortfolioNote(String portfolioId, Long noteId);
}
