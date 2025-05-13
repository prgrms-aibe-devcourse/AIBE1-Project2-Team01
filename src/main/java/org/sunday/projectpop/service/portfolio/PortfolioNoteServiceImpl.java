package org.sunday.projectpop.service.portfolio;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.exceptions.FileManagementException;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.exceptions.PortfolioNoteNotFound;
import org.sunday.projectpop.exceptions.UnauthorizedException;
import org.sunday.projectpop.model.dto.*;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioNote;
import org.sunday.projectpop.model.entity.PortfolioNoteFile;
import org.sunday.projectpop.model.repository.PortfolioNoteFileRepository;
import org.sunday.projectpop.model.repository.PortfolioNoteRepository;
import org.sunday.projectpop.model.repository.PortfolioRepository;
import org.sunday.projectpop.service.feedback.AnalysisService;
import org.sunday.projectpop.service.upload.FileStorageService;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final PortfolioNoteFileRepository portfolioNoteFileRepository;
    private final AnalysisService analysisService;

    @Override
    @Transactional
    public PortfolioNote createNote(String userId, String portfolioId, PortfolioNoteCreateRequest request, List<MultipartFile> files) {
        // 해당 포트폴리오 있는지 확인
        Portfolio portfolio = findPortfolio(portfolioId);

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
                .filter(file -> !file.isEmpty()) // 추가: 파일이 비어있지 않은 경우만 처리
                .map(file -> fileStorageService
                        .uploadPortfolioNoteFile(file, portfolioNote))
                .toList();
        portfolioNote.setFiles(fileList);
        PortfolioNote savedNote = portfolioNoteRepository.save(portfolioNote);

        // 노트 등록시 원활한 피드백요청을 위해 summary 확인해
        analysisService.handleNoteSubmit(portfolio);

        return savedNote;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioNoteResponse> getPortfolioNoteList(String portfolioId) {
        // 해당 포트폴리오 확인
        Portfolio portfolio = findPortfolio(portfolioId);

        List<PortfolioNote> noteList = findAll(portfolio);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        List<PortfolioNoteResponse> responseList = new ArrayList<>();
        for (PortfolioNote note : noteList) {
            PortfolioNoteResponse portfolioNoteResponse = new PortfolioNoteResponse(
                    note.getId(),
                    note.getContent(),
                    note.getCreatedAt().format(formatter),
                    !note.getFiles().isEmpty()
            );
            responseList.add(portfolioNoteResponse);
        }
        return responseList;
    }

    private Portfolio findPortfolio(String portfolioId) {
        return portfolioRepository.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));
    }

    private List<PortfolioNote> findAll(Portfolio portfolio) {
        List<PortfolioNote> noteList = portfolioNoteRepository.findAllByPortfolio(portfolio);
        if (noteList.isEmpty()) {
            throw new PortfolioNoteNotFound("등록된 노트가 없습니다.");
        }
        return noteList;
    }

    private PortfolioNote findPortfolioNote(Long noteId) {
        return portfolioNoteRepository.findById(noteId)
                .orElseThrow(() -> new PortfolioNoteNotFound("해당 노트를 찾을 수 없습니다."));
    }

    private void checkByUserId(Portfolio portfolio, String userId) {
        log.info("checkByUserId: %s %s".formatted(portfolio.getUserId(), userId));
        if (!portfolio.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 노트에 대한 권한이 없습니다.");
        }
    }

    private void checkByNote(Portfolio portfolio, PortfolioNote note) {
        log.info("checkByNote: %s %s".formatted(portfolio, note.getPortfolio()));
        if (!note.getPortfolio().equals(portfolio))
            throw new UnauthorizedException("해당 노트에 대한 권한이 없습니다.");
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioNoteDetailResponse getPortfolioNote(String portfolioId, Long noteId) {
        Portfolio portfolio = findPortfolio(portfolioId);
        PortfolioNote note = findPortfolioNote(noteId);

        if (!note.getPortfolio().equals(portfolio)) {
            throw new UnauthorizedException("해당 노트에 대한 권한이 없습니다.");
        }

        List<FileResponse> files = note.getFiles()
                .stream()
                .map(file -> new FileResponse(
                        file.getPortfolioNoteFileId(),
                        file.getOriginalFilename(),
                        file.getStoredUrl(),
                        file.getFileType()
                ))
                .toList();

        return new PortfolioNoteDetailResponse(
                note.getId(),
                note.getContent(),
                note.getCreatedAt().toString(),
                files
        );
    }

    @Override
    @Transactional
    public void updatePortfolioNote(String userId, String portfolioId, Long noteId, PortfolioNoteUpdateRequest request, List<MultipartFile> newFiles) throws Exception {
        // 해당 포트폴리오 유무 및 자격 확인
        Portfolio portfolio = findPortfolio(portfolioId);
        checkByUserId(portfolio, userId);

        // 해당 노트 유무 및 자격 확인
        PortfolioNote note = findPortfolioNote(noteId);
        checkByNote(portfolio, note);

        // 내용 업데이트
        note.setContent(request.content());

        // 요청 파일 제거
        if (request.deleteFileIds() != null) {
            for (Long fileId : request.deleteFileIds()) {
                deleteFile(fileId);
            }
        }

        // 새 파일 업로드
        if (newFiles != null && !newFiles.isEmpty()) {
            for (MultipartFile file : newFiles) {
                if (!file.isEmpty()) {
                    PortfolioNoteFile uploaded = fileStorageService.uploadPortfolioNoteFile(file, note);
                    note.getFiles().add(uploaded);
                }
            }
        }

        portfolioNoteRepository.save(note);
    }

    private void deleteFile(Long fileId) {
        PortfolioNoteFile file = portfolioNoteFileRepository.findById(fileId)
                .orElseThrow();
        portfolioNoteFileRepository.delete(file);
        try {
            fileStorageService.deleteFile(file.getStoredFilename());
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new FileManagementException("파일 삭제에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public void deletePortfolioNote(String userId, String portfolioId, Long noteId) {
        Portfolio portfolio = findPortfolio(portfolioId);
        checkByUserId(portfolio, userId);

        PortfolioNote note = findPortfolioNote(noteId);
        checkByNote(portfolio, note);

        // 스토리지에서 파일 삭제
        if (note.getFiles() != null) {
            for (PortfolioNoteFile file : note.getFiles()) {
                deleteFile(file.getPortfolioNoteFileId());
            }
        }
        portfolioNoteRepository.delete(note);
    }
}
