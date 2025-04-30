package org.sunday.projectpop.controller.portfolio;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.PortfolioCreateRequest;
import org.sunday.projectpop.model.dto.PortfolioResponse;
import org.sunday.projectpop.model.dto.PortfolioRetrospectiveRequest;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.service.portfolio.PortfolioService;
import org.sunday.projectpop.service.portfolio.RetrospectiveService;

import java.util.List;

@RestController
@RequestMapping("/portfolios")
@RequiredArgsConstructor
public class PortfolioRestController {

    private final PortfolioService portfolioService;
    private final RetrospectiveService retrospectiveService;

    // TODO: 내 포트폴리오 조회 (all)
    @GetMapping("/me")
    public ResponseEntity<?> getMyPortfolios() {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        List<Portfolio> portfolios = portfolioService.getMyPortfolios(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(portfolios);
    }

    // 포트폴리오 등록
    @PostMapping
    public ResponseEntity<Void> addPortfolio(@Valid @RequestBody PortfolioCreateRequest request) {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        portfolioService.createPortfolio(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    // 포트폴리오 상세 조회
    // TODO: 회고 column 추가해서 보이기
    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponse> getPortfolio(@PathVariable String portfolioId) {
        PortfolioResponse response = portfolioService.getPortfolio(portfolioId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 포트폴리오 수정
    @PutMapping("/{portfolioId}")
    public ResponseEntity<Void> updatePortfolio(@PathVariable String portfolioId,
                                                @Valid @RequestBody PortfolioCreateRequest request) {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        portfolioService.updatePortfolio(userId, portfolioId, request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    // 포트폴리오 삭제
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable String portfolioId) {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        portfolioService.deletePortfolio(userId, portfolioId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    // TODO: 포트폴리오에 대한 개인 생각 조회 (all)

    // 포트폴리오에 대한 개인 생각(회고) 등록
    @PostMapping("/{portfolioId}/retrospectives")
    public ResponseEntity<Void> addPortfolioRetrospective(
            @PathVariable String portfolioId,
            @Valid @RequestBody PortfolioRetrospectiveRequest request) {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        retrospectiveService.addRetrospective(portfolioId, userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }


    // TODO: 포트폴리오에 대한 개인 생각 상세


    // TODO: 포트폴리오에 대한 개인 생각 수정
    // TODO: 포트폴리오에 대한 개인 생각 삭제
}
