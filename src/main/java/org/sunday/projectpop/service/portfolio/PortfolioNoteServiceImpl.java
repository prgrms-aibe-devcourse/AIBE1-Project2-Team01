package org.sunday.projectpop.service.portfolio;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.exceptions.UnauthorizedException;
import org.sunday.projectpop.model.dto.PortfolioNoteCreateRequest;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioNote;
import org.sunday.projectpop.model.entity.PortfolioNoteFile;
import org.sunday.projectpop.model.repository.PortfolioNoteRepository;
import org.sunday.projectpop.model.repository.PortfolioRepository;
import org.sunday.projectpop.service.upload.FileStorageService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log
public class PortfolioNoteServiceImpl implements PortfolioNoteService {

    private final PortfolioNoteRepository portfolioNoteRepository;
    private final FileStorageService fileStorageService;
    private final PortfolioRepository portfolioRepository;

    @Override
    public void createNote(String userId, String portfolioId, PortfolioNoteCreateRequest request, List<MultipartFile> files) {
        // 해당 포트폴리오 있는지 확인
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));

        // 해당 포트폴리오에 대한 권한 확인
        if (!portfolio.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 포트폴리오에 대한 권한이 없습니다.");
        }

        PortfolioNote portfolioNote = new PortfolioNote();
        portfolioNote.setPortfolio(portfolio);
        portfolioNote.setContent(request.content());
        portfolioNote.setUserId(portfolio.getUserId());

        List<PortfolioNoteFile> fileList = Optional.ofNullable(files)
                .orElse(Collections.emptyList())
                .stream()
                .map(file -> fileStorageService
                        .uploadPortfolioNoteFile(file, portfolioNote))
                .toList();
        portfolioNote.setFiles(fileList);
        portfolioNoteRepository.save(portfolioNote);
    }
}
