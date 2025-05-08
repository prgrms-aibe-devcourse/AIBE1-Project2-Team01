package org.sunday.projectpop.service.portfolio;

import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.model.dto.*;
import org.sunday.projectpop.model.entity.PortfolioNote;

import java.util.List;

public interface PortfolioNoteService {

    PortfolioNote createNote(String userId,
                             String portfolioId,
                             PortfolioNoteCreateRequest request,
                             List<MultipartFile> files);

    List<PortfolioNoteResponse> getPortfolioNoteList(String portfolioId);

    PortfolioNoteDetailResponse getPortfolioNote(String portfolioId, Long noteId);

    void updatePortfolioNote(String userId,
                             String portfolioId,
                             Long noteId,
                             PortfolioNoteUpdateRequest request,
                             List<MultipartFile> newFiles) throws Exception;

    void deletePortfolioNote(String userId, String portfolioId, Long noteId);
}
