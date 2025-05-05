package org.sunday.projectpop.service.portfolio;

import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.model.dto.*;

import java.util.List;

public interface PortfolioNoteService {

    void createNote(String userId,
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
}
