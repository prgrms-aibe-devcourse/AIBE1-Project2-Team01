package org.sunday.projectpop.controller.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.EnhancedSuggestionDto;
import org.sunday.projectpop.model.dto.SuggestionDto;
import org.sunday.projectpop.model.entity.Project;
import org.sunday.projectpop.model.entity.SuggestFromLeader;
import org.sunday.projectpop.model.entity.UserAccount;
import org.sunday.projectpop.model.repository.ProjectRepository;
import org.sunday.projectpop.model.repository.UserAccountRepository;
import org.sunday.projectpop.model.repository.message.SuggestFromLeaderRepository;
import org.sunday.projectpop.service.message.SuggestFromLeaderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/suggestions")
public class SuggestController {

    private final SuggestFromLeaderService service;
    private final UserAccountRepository userAccountRepository;
    private final ProjectRepository projectRepository;
    private final SuggestFromLeaderRepository suggestFromLeaderRepository;

    @PostMapping("/send")
    public ResponseEntity<Void> sendSuggestion(
            @RequestParam String senderId,// <- 임시로 받음 @AuthenticationPrincipal CustomUserDetails userDetails
            @RequestParam String receiverId,
            @RequestParam String projectId,
            @RequestParam String message
    ) {
        //        UserAccount sender = userDetails.getUserAccount();
        UserAccount sender = userAccountRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("보내는 유저 없음"));
        UserAccount receiver = userAccountRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("받는 유저 없음"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트 없음"));

        service.suggest(project, sender, receiver, message);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptSuggestion(@PathVariable Long id) {
        service.accept(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/received")
    public ResponseEntity<List<EnhancedSuggestionDto>> getSuggestionsForReceiver(@RequestParam String receiverId) {
        UserAccount receiver = userAccountRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("받는 유저 없음"));
        List<EnhancedSuggestionDto> dtoList = service.getReceivedSuggestions(receiver).stream()
                .map(EnhancedSuggestionDto::fromEntity)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<EnhancedSuggestionDto>> getSuggestionsFromSender(@RequestParam String senderId) {
        UserAccount sender = userAccountRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("보내는 유저 없음"));
        List<EnhancedSuggestionDto> dtoList = service.getSentSuggestions(sender).stream()
                .map(EnhancedSuggestionDto::fromEntity)
                .toList();
        return ResponseEntity.ok(dtoList);
    }
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectSuggestion(@PathVariable Long id) {
        service.reject(id); // 서비스단에 reject 구현 필요
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/toggle-read")
    public ResponseEntity<Void> toggleSuggestionRead(@PathVariable Long id) {
        SuggestFromLeader suggestion = suggestFromLeaderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("제안을 찾을 수 없습니다."));
        suggestion.setChecking(!suggestion.isChecking());
        suggestFromLeaderRepository.save(suggestion);
        return ResponseEntity.ok().build();
    }



}