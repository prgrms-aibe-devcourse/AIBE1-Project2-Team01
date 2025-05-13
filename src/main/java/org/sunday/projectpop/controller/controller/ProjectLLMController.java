package org.sunday.projectpop.controller.controller;

import lombok.RequiredArgsConstructor;
import org.sunday.projectpop.model.dto.GeminiResponse;
import org.sunday.projectpop.service.project.GeminiLLMService;
import org.sunday.projectpop.service.project.ProjectLLMService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectLLMController {

    private final ProjectLLMService projectLLMService;
    private final GeminiLLMService geminiLLMService;

    // 로그인한 유저의 프로필 + 기술 태그 기반으로 prompt 문자열 생성
    @GetMapping("/generate")
    public ResponseEntity<String> generatePrompt(@AuthenticationPrincipal UserDetails userDetails) {
        // String userId = userDetails.getUsername(); // 현재 로그인된 유저의 ID (String)
        String userId = "u01";

        String prompt = projectLLMService.generatePrompt(userId); // prompt 생성 로직 호출

        return ResponseEntity.ok(prompt);
    }
    @GetMapping("/generate/execute")
    public ResponseEntity<GeminiResponse> generateProjectFromLLM(@AuthenticationPrincipal UserDetails userDetails) {
        // String userId = userDetails.getUsername();
        String userId = "u01";
        String prompt = projectLLMService.generatePrompt(userId); // 기존 프롬프트 생성
        GeminiResponse result = geminiLLMService.getGeneratedProject(prompt); // Gemini 호출
        projectLLMService.saveGeneratedProject(result, userId);
        return ResponseEntity.ok(result); // JSON 형태로 응답
    }
}
