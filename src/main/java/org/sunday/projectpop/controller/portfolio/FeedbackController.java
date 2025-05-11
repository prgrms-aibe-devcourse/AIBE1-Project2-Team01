package org.sunday.projectpop.controller.portfolio;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.FeedbackResponse;
import org.sunday.projectpop.service.feedback.FeedbackService;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // 피드백 생성 요청
    @PostMapping("/request/{portfolioId}/{noteId}")
    public ResponseEntity<FeedbackResponse> requestFeedback(
            @PathVariable String portfolioId,
            @PathVariable Long noteId) {
        FeedbackResponse response = feedbackService.generatePortfolioFeedback(portfolioId, noteId);
        return ResponseEntity.ok(response);
    }

    // 피드백 상태 조회
    @GetMapping("/{portfolioId}/{noteId}/{feedbackId}")
    public ResponseEntity<FeedbackResponse> getFeedback(
            @PathVariable String portfolioId,
            @PathVariable Long noteId,
            @PathVariable Long feedbackId) {

        FeedbackResponse response = feedbackService.getFeedback(feedbackId);
        return ResponseEntity.ok(response);
    }

    // 최신 피드백 조회
    @GetMapping("/{portfolioId}/{noteId}/latest")
    public ResponseEntity<FeedbackResponse> getLatestFeedback(
            @PathVariable String portfolioId,
            @PathVariable Long noteId) {

        FeedbackResponse response = feedbackService.getLatestFeedback(portfolioId, noteId);
        return ResponseEntity.ok(response);
    }


    // 피드백 목록/단건 조회
    @GetMapping("/{portfolioId}")
    public ResponseEntity<?> feedbackList(@PathVariable String portfolioId) {
        List<FeedbackResponse> feedbackList = feedbackService.getFeedbackList(portfolioId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(feedbackList);
    }

    // 피드백 상세 내용 조회
}
