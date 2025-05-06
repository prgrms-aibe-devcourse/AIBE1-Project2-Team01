package org.sunday.projectpop.controller.portfolio;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.service.feedback.FeedbackService;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // 피드백 생성 요청
    @PostMapping("/request/{portfolioId}")
    public ResponseEntity<Void> requestFeedback(@PathVariable String portfolioId) {
        feedbackService.generateFeedback(portfolioId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    // 피드백 목록/단건 조회

    // 피드백 상세 내용 조회
}
