package org.sunday.projectpop.controller.portfolio;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sunday.projectpop.model.dto.PortfolioCreateRequest;
import org.sunday.projectpop.service.portfolio.PortfolioService;

@RestController
@RequestMapping("/portfolios")
public class PortfolioRestController {
    private final PortfolioService portfolioService;

    public PortfolioRestController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // TODO: 내 포트폴리오 조회 (all)

    // 포트폴리오 등록
    @PostMapping
    public ResponseEntity<Void> addPortfolio(@Valid @RequestBody PortfolioCreateRequest request) {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        portfolioService.createPortfolio(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    // TODO: 포트폴리오 상세 조회
    // TODO: 포트폴리오 수정
    // TODO: 포트폴리오 삭제
    // TODO: 포트폴리오에 대한 개인 생각 조회 (all)
    // TODO: 포트폴리오에 대한 개인 생각 상세
    // TODO: 포트폴리오에 대한 개인 생각 등록
    // TODO: 포트폴리오에 대한 개인 생각 수정
    // TODO: 포트폴리오에 대한 개인 생각 삭제
}
