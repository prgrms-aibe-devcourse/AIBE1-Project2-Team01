package org.sunday.projectpop.controller.portfolio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.model.dto.*;
import org.sunday.projectpop.model.entity.PortfolioNote;
import org.sunday.projectpop.model.enums.PortfolioType;
import org.sunday.projectpop.service.portfolio.PortfolioNoteService;
import org.sunday.projectpop.service.portfolio.PortfolioService;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final PortfolioNoteService portfolioNoteService;

    // 포트폴리오 목록 조회
    @GetMapping
    public String getMyPortfolios(Model model) {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        List<PortfolioSimple> portfolios = portfolioService.getMyPortfolios(userId);
        model.addAttribute("portfolios", portfolios);
        model.addAttribute("userId", userId);
        model.addAttribute("title", "포트폴리오 목록");
        model.addAttribute("viewName", "portfolio/list");

        return "portfolio/layout";
    }

    // 포트폴리오 등록 폼
    @GetMapping("/new")
    public String portfolioForm(Model model) {
        model.addAttribute("portfolio", PortfolioRequest.emptyCreateRequest());
        model.addAttribute("allTypes", PortfolioType.values());
        model.addAttribute("title", "포트폴리오 등록");
        model.addAttribute("viewName", "portfolio/form");
        return "portfolio/layout";
    }

    // 포트폴리오 등록
    @PostMapping(value="/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addPortfolio(
            @Valid @ModelAttribute("portfolio") PortfolioRequest request,
            BindingResult bindingResult,
//            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            Model model) {

        String userId = "dummy1"; // TODO: Authentication에서 userId 받기

        if (bindingResult.hasErrors()) {
            model.addAttribute("portfolio", request);
            model.addAttribute("allTypes", PortfolioType.values());
            model.addAttribute("title", "포트폴리오 등록");
            model.addAttribute("viewName", "portfolio/form");
            return "portfolio/layout";
        }
//        System.out.println(files);
        portfolioService.createPortfolio(userId, request);
        return "redirect:/portfolios";
    }

    // 포트폴리오 상세 조회
    @GetMapping("/{portfolioId}")
    public String getPortfolio(@PathVariable String portfolioId, Model model) {
//        PortfolioDetailResponse portfolioDetail = portfolioService.getPortfolioDetail(portfolioId);
        PortfolioResponse portfolio = portfolioService.getPortfolio(portfolioId);
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("title", "포트폴리오 상세");
        model.addAttribute("viewName", "portfolio/details");
        return "portfolio/layout";
    }

    // 포트폴리오 수정 폼
    @GetMapping("/{portfolioId}/edit")
    public String updatePortfolioForm(@PathVariable String portfolioId, Model model) {

        String userId = "dummy1"; // TODO: Authentication에서 userId 받기

        model.addAttribute("portfolio", portfolioService.getPortfolio(portfolioId));
        model.addAttribute("allTypes", PortfolioType.values());
        model.addAttribute("title", "포트폴리오 수정");
        model.addAttribute("viewName", "portfolio/form");
        return "portfolio/layout";
    }

    // 포트폴리오 수정
    @PostMapping("/{portfolioId}/edit")
    public String updatePortfolio(
            @PathVariable String portfolioId,
            @Valid @ModelAttribute("portfolio") PortfolioRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기

        if (bindingResult.hasErrors()) {
            model.addAttribute("portfolio", request);
            model.addAttribute("allTypes", PortfolioType.values());
            model.addAttribute("title", "포트폴리오 수정");
            model.addAttribute("viewName", "portfolio/form");
            return "portfolio/layout";
        }
        portfolioService.updatePortfolio(userId, portfolioId, request);
        return "redirect:/portfolios/{portfolioId}";
    }

    // 포트폴리오 삭제
    @DeleteMapping("/{portfolioId}")
    public String deletePortfolio(@PathVariable String portfolioId) {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        portfolioService.deletePortfolio(userId, portfolioId);
        return "redirect:/portfolios";
    }

    // 포트폴리오에 대한 노트 조회 목록
    @GetMapping("/{portfolioId}/notes")
    @ResponseBody
    public ResponseEntity<List<PortfolioNoteResponse>> getPortfolioNoteList(@PathVariable String portfolioId) {
        List<PortfolioNoteResponse> notes = portfolioNoteService.getPortfolioNoteList(portfolioId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notes);
    }

    @PostMapping("/{portfolioId}/notes")
    @ResponseBody
    public PortfolioNoteResponse addPortfolioNote(
            @PathVariable String portfolioId,
            @Valid @RequestBody PortfolioNoteCreateRequest request) {

        System.out.println("---------------------------");
        System.out.println(portfolioId);
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
//        portfolioNoteService.createNote(userId, portfolioId, request, files);
        PortfolioNote note = portfolioNoteService.createNote(userId, portfolioId, request, null);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        return new PortfolioNoteResponse(
                note.getId(),
                note.getContent(),
                note.getCreatedAt().format(formatter),
                false
        );
    }
/*
    // 포트폴리오에 대한 노트 등록
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
*/

    // 포트폴리오에 대한 노트 상세
    @GetMapping("/{portfolioId}/notes/{noteId}")
    public ResponseEntity<?> getPortfolioNote(@PathVariable String portfolioId, @PathVariable Long noteId) {
        PortfolioNoteDetailResponse response = portfolioNoteService.getPortfolioNote(portfolioId, noteId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    // 포트폴리오에 대한 노트 수정
    @Operation(summary = "포트폴리오-노트 수정", description = "파일과 JSON 데이터를 함께 업로드합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(name = "request", contentType = "application/json"),
                                    @Encoding(name = "newFiles", contentType = "application/octet-stream")
                            }
                    )
            )
    )
    @PutMapping(value = "/{portfolioId}/notes/{noteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePortfolioNote(
            @PathVariable String portfolioId,
            @PathVariable Long noteId,
            @Valid @RequestPart("request") PortfolioNoteUpdateRequest request,
            @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles
    ) throws Exception {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        portfolioNoteService.updatePortfolioNote(userId, portfolioId, noteId, request, newFiles);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    // 포트폴리오에 대한 노트 삭제
    @DeleteMapping("/{portfolioId}/{noteId}")
    public ResponseEntity<Void> deletePortfolioNote(
            @PathVariable String portfolioId,
            @PathVariable Long noteId) {
        String userId = "dummy1"; // TODO: Authentication에서 userId 받기
        portfolioNoteService.deletePortfolioNote(userId, portfolioId, noteId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
