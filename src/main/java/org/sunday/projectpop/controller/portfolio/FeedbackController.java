package org.sunday.projectpop.controller.portfolio;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.FeedbackResponse;
import org.sunday.projectpop.service.feedback.FeedbackService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // 포트폴리오 요약 확인
    @GetMapping("/{portfolioId}/summary-status")
    public ResponseEntity<Map<String, Boolean>> getSummaryStatus(@PathVariable String portfolioId) {
        boolean isSummarized = feedbackService.checkSummaryStatus(portfolioId);
        return ResponseEntity.ok(Collections.singletonMap("isSummarized", isSummarized));
    }


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
