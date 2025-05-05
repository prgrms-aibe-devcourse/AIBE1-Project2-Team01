package org.sunday.projectpop.service.portfolio;

import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.model.dto.PortfolioNoteCreateRequest;

import java.util.List;

public interface PortfolioNoteService {

    void createNote(String userId, String portfolioId, PortfolioNoteCreateRequest request, List<MultipartFile> files);
}
