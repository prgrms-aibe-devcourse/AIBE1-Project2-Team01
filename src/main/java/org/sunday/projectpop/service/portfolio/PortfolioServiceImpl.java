package org.sunday.projectpop.service.portfolio;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.exceptions.FileUploadFailureException;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.exceptions.UnauthorizedException;
import org.sunday.projectpop.model.dto.FileResponse;
import org.sunday.projectpop.model.dto.PortfolioCreateRequest;
import org.sunday.projectpop.model.dto.PortfolioResponse;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioFile;
import org.sunday.projectpop.model.entity.PortfolioUrl;
import org.sunday.projectpop.model.repository.PortfolioRepository;
import org.sunday.projectpop.service.upload.FileStorageService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final FileStorageService fileStorageService;

    @Override
    public void createPortfolio(String userId, PortfolioCreateRequest request, List<MultipartFile> files) {
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
        List<PortfolioUrl> urls = Optional.ofNullable(request.urls())
                .orElse(Collections.emptyList())
                .stream()
                .map(url -> new PortfolioUrl(url, portfolio))
                .toList();
        portfolio.setUrls(urls);

        List<PortfolioFile> fileList = Optional.ofNullable(files)
                .orElse(Collections.emptyList())
                .stream()
                .map(file -> {
                    String storedUrl = null;
                    try {
                        storedUrl = fileStorageService.uploadAndGenerateSignedUrl(file, 3600);
                    } catch (Exception e) {
                        log.severe(e.getMessage());
                        throw new FileUploadFailureException("파일 업로드에 실패했습니다.");
                    }
                    String fileType = file.getContentType();
                    return new PortfolioFile(file.getOriginalFilename(), storedUrl, fileType, portfolio);
                })
                .toList();
        log.info(fileList.toString());
        portfolio.setFiles(fileList);

        portfolioRepository.save(portfolio);
    }

    @Override
    public List<Portfolio> getMyPortfolios(String userId) {
        // TODO: userId 한번더 체크
//        UserAccount userAccount = userAccountRepository.findByUserId(userId).orElseThrow(
//        () -> new UserNotFoundException("유저를 찾을 수 없습니다."));

//        portfolioRepository.findAllByUserId(userAccount.getUserId())
        List<Portfolio> portfolios = portfolioRepository.findAllByUserId(userId);
        if (portfolios.isEmpty()) {
            throw new PortfolioNotFoundException("등록된 포트폴리오가 없습니다.");
        }
        return portfolios;
    }

    @Override
    public PortfolioResponse getPortfolio(String portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));

        List<String> urls = portfolio.getUrls()
                .stream()
                .map(PortfolioUrl::getUrl)
                .toList();

        List<FileResponse> files = portfolio.getFiles()
                .stream()
                .map(file -> new FileResponse(
                        file.getOriginalFilename(),
                        file.getStoredUrl(),
                        file.getFileType()
                ))
                .toList();

        return new PortfolioResponse(
                portfolio.getPortfolioId(),
                portfolio.getPortfolioType(),
                portfolio.getTitle(),
                portfolio.getDescription(),
                portfolio.getCreatedAt().toString(),
                urls,
                files
        );

    }

    @Override
    public void updatePortfolio(String userId, String portfolioId, PortfolioCreateRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 포트폴리오를 수정할 권한이 없습니다.");
        }

        portfolio.setPortfolioType(request.portfolioType());
//        portfolio.setUrl(request.url());
        portfolio.setDescription(request.description());
        portfolioRepository.save(portfolio);
    }

    @Override
    public void deletePortfolio(String userId, String portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 포트폴리오를 삭제할 권한이 없습니다.");
        }

        portfolioRepository.delete(portfolio);
    }
}

