package org.sunday.projectpop.controller.portfolio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.model.dto.PortfolioCreateRequest;
import org.sunday.projectpop.model.dto.PortfolioNoteCreateRequest;
import org.sunday.projectpop.model.dto.PortfolioResponse;
import org.sunday.projectpop.model.dto.PortfolioUpdateRequest;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.service.portfolio.PortfolioNoteService;
import org.sunday.projectpop.service.portfolio.PortfolioService;

import java.util.List;

@RestController
@RequestMapping("/portfolios")
@RequiredArgsConstructor
public class PortfolioRestController {

    private final PortfolioService portfolioService;
    private final PortfolioNoteService portfolioNoteService;

    // 포트폴리오 목록 조회
    @GetMapping("/me")
    public ResponseEntity<?> getMyPortfolios() {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        List<Portfolio> portfolios = portfolioService.getMyPortfolios(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(portfolios);
    }

    // 포트폴리오 등록
    @Operation(summary = "포트폴리오 업로드", description = "파일과 JSON 데이터를 함께 업로드합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(name = "request", contentType = "application/json"),
                                    @Encoding(name = "files", contentType = "application/octet-stream")
                            }
                    )
            )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addPortfolio(
            @Valid @RequestPart("request") PortfolioCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        portfolioService.createPortfolio(userId, request, files);
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
    public ResponseEntity<Void> updatePortfolio(
            @PathVariable String portfolioId,
            @Valid @RequestPart("request") PortfolioUpdateRequest request,
            @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles

    ) {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        portfolioService.updatePortfolio(userId, portfolioId, request, newFiles);
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

    // TODO: 포트폴리오에 대한 노트 조회 목록
    // TODO: 포트폴리오에 대한 노트 등록
    @Operation(summary = "포트폴리오-노트 업로드", description = "파일과 JSON 데이터를 함께 업로드합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(name = "request", contentType = "application/json"),
                                    @Encoding(name = "files", contentType = "application/octet-stream")
                            }
                    )
            )
    )
    @PostMapping(value = "/{portfolioId}/notes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addPortfolioNote(
            @PathVariable String portfolioId,
            @Valid @RequestPart("request") PortfolioNoteCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        portfolioNoteService.createNote(userId, portfolioId, request, files);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
    // TODO: 포트폴리오에 대한 노트 상세



    // TODO: 포트폴리오에 대한 노트 수정
    // TODO: 포트폴리오에 대한 노트 삭제
}
