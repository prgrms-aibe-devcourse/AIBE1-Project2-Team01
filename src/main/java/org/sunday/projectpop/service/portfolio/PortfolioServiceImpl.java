package org.sunday.projectpop.service.portfolio;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.exceptions.FileManagementException;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.exceptions.UnauthorizedException;
import org.sunday.projectpop.model.dto.*;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioFile;
import org.sunday.projectpop.model.entity.PortfolioSummary;
import org.sunday.projectpop.model.entity.PortfolioUrl;
import org.sunday.projectpop.model.enums.AnalysisStatus;
import org.sunday.projectpop.model.repository.PortfolioFileRepository;
import org.sunday.projectpop.model.repository.PortfolioRepository;
import org.sunday.projectpop.model.repository.PortfolioSummaryRepository;
import org.sunday.projectpop.model.repository.PortfolioUrlRepository;
import org.sunday.projectpop.service.feedback.AnalysisService;
import org.sunday.projectpop.service.upload.FileStorageService;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final FileStorageService fileStorageService;
    private final PortfolioFileRepository portfolioFileRepository;
    private final PortfolioUrlRepository portfolioUrlRepository;
    private final AnalysisService analysisService;
    private final PortfolioSummaryRepository portfolioSummaryRepository;

    @Override
    @Transactional
    public void createPortfolio(String userId, PortfolioRequest request) {
        // TODO: userId 한번더 체크
//        UserAccount userAccount = userAccountRepository.findByUserId(userId).orElseThrow(
//        () -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        Portfolio portfolio = new Portfolio();
//        portfolio.setUserId(userAccount.getUserId());
        portfolio.setUserId(userId);
        portfolio.setPortfolioType(request.portfolioType());
        portfolio.setTitle(request.title());
        portfolio.setDescription(request.description());

        // URL 저장
        List<PortfolioUrl> urls = Optional.ofNullable(request.newUrls())
                .orElse(Collections.emptyList())
                .stream()
                .map(url -> new PortfolioUrl(url, portfolio))
                .toList();
        portfolio.setUrls(urls);

        // 파일 업로드 및 저장
        List<PortfolioFile> fileList = Optional.ofNullable(request.files())
                .orElse(Collections.emptyList())
                .stream()
                .filter(file -> !file.isEmpty()) // 추가: 파일이 비어있지 않은 경우만 처리
                .map(file ->
                        fileStorageService.uploadPortfolioFile(file, portfolio))
                .toList();
        log.info(fileList.toString());
        portfolio.setFiles(fileList);


        // 요약을 위한 분석 생성
        PortfolioSummary summary = PortfolioSummary.builder()
                .status(AnalysisStatus.NOT_STARTED)
                .build();
        portfolioSummaryRepository.save(summary);

        portfolio.setSummary(summary);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        // 비동기 요약 요청
        analysisService.handleAnalysis(savedPortfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioSimple> getMyPortfolios(String userId) {
        // TODO: userId 한번더 체크
//        UserAccount userAccount = userAccountRepository.findByUserId(userId).orElseThrow(
//        () -> new UserNotFoundException("유저를 찾을 수 없습니다."));

//        portfolioRepository.findAllByUserId(userAccount.getUserId())
        List<Portfolio> portfolios = portfolioRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        if (portfolios.isEmpty()) {
//            throw new PortfolioNotFoundException("등록된 포트폴리오가 없습니다.");
            return Collections.emptyList();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));
        return portfolios.stream()
                .map(portfolio -> new PortfolioSimple(
                        portfolio.getPortfolioId(),
                        portfolio.getPortfolioType(),
                        portfolio.getTitle(),
                        portfolio.getCreatedAt().format(formatter)
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio(String portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));

        List<UrlResponse> urls = portfolio.getUrls()
                .stream()
                .map(url -> new UrlResponse(
                        url.getPortfolioUrlId(),
                        url.getUrl()
                ))
                .toList();

        List<FileResponse> files = portfolio.getFiles()
                .stream()
                .map(file -> {
                    String newUrl = null;
                    try {
                        newUrl = fileStorageService.generateSignedUrl(file.getStoredFilename(), 3600);
                    } catch (Exception e) {
                        throw new FileManagementException("파일 URL 생성에 실패했습니다.");
                    }
                    return new FileResponse(
                            file.getPortfolioFileId(),
                            file.getOriginalFilename(),
                            newUrl,
                            file.getFileType());
                })
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        return new PortfolioResponse(
                portfolio.getPortfolioId(),
                portfolio.getPortfolioType(),
                portfolio.getTitle(),
                portfolio.getDescription(),
                portfolio.getCreatedAt().format(formatter),
                urls,
                files
        );
    }

    @Override
    @Transactional
    public void updatePortfolio(String userId, String portfolioId, PortfolioRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 포트폴리오를 수정할 권한이 없습니다.");
        }

        portfolio.setPortfolioType(request.portfolioType());
        portfolio.setTitle(request.title());
        portfolio.setDescription(request.description());

        // 삭제할 파일 처리
        try {
            deletePortfolioFiles(request.deleteFileIds());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        deletePortfolioUrls(request.deleteUrlIds());

        // 파일 업로드
        if (request.files() != null) {
            for (MultipartFile file : request.files()) {
                if (!file.isEmpty()) {
                    PortfolioFile uploaded = fileStorageService.uploadPortfolioFile(file, portfolio);
                    portfolio.getFiles().add(uploaded);
                }
            }
        }
        // 중복 URL 검증 후 등록
        Set<String> existingUrls = portfolio.getUrls()
                .stream()
                .map(PortfolioUrl::getUrl)
                .collect(Collectors.toSet());

        if (request.newUrls() != null) {
            for (String url : request.newUrls()) {
                if (!existingUrls.contains(url)) {
                    PortfolioUrl portfolioUrl = new PortfolioUrl(url, portfolio);
                    portfolio.getUrls().add(portfolioUrl);
                }
            }
        }

        PortfolioSummary summary = portfolio.getSummary();
        summary.setStatus(AnalysisStatus.NOT_STARTED);
        portfolioSummaryRepository.save(summary);
        portfolio.setSummary(summary);

        // 업데이트
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        // 비동기 요약 요청
        analysisService.handleAnalysis(savedPortfolio);

    }

    private void deletePortfolioUrls(List<Long> urlIds) {
        if (urlIds == null || urlIds.isEmpty()) return;

        List<PortfolioUrl> urls = portfolioUrlRepository.findAllByPortfolioUrlIdIn(urlIds);
        portfolioUrlRepository.deleteAll(urls);
    }

    private void deletePortfolioFiles(List<Long> fileIds) throws Exception {
        if (fileIds == null || fileIds.isEmpty()) return;

        List<PortfolioFile> files = portfolioFileRepository.findAllByPortfolioFileIdIn(fileIds);
        for (PortfolioFile file : files) {
            fileStorageService.deleteFile(file.getStoredFilename());
            portfolioFileRepository.delete(file);
        }
    }

    @Override
    @Transactional
    public void deletePortfolio(String userId, String portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 포트폴리오를 삭제할 권한이 없습니다.");
        }

        // 스토리지에서 파일 삭제
        if (portfolio.getFiles() != null) {
            for (PortfolioFile file : portfolio.getFiles()) {
                try {
                    fileStorageService.deleteFile(file.getStoredFilename());
                } catch (Exception e) {
                    throw new FileManagementException("파일 삭제에 실패했습니다. " + e.getMessage());
                }
            }
        }

        portfolioRepository.delete(portfolio);
    }
}

