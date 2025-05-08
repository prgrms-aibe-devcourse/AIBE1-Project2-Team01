package org.sunday.projectpop.controller.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.entity.Project;
import org.sunday.projectpop.model.entity.SuggestFromLeader;
import org.sunday.projectpop.model.entity.UserAccount;
import org.sunday.projectpop.model.repository.ProjectRepository;
import org.sunday.projectpop.model.repository.UserAccountRepository;
import org.sunday.projectpop.service.message.SuggestFromLeaderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/suggestions")
public class SuggestController {

    private final SuggestFromLeaderService service;
    private final UserAccountRepository userAccountRepository;
    private final ProjectRepository projectRepository;

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
    public ResponseEntity<List<SuggestFromLeader>> getSuggestionsForReceiver(@RequestParam String receiverId) {
        UserAccount receiver = userAccountRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("받는 유저 없음"));
        return ResponseEntity.ok(service.getReceivedSuggestions(receiver));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<SuggestFromLeader>> getSuggestionsFromSender(@RequestParam String senderId) {
        UserAccount sender = userAccountRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("보내는 유저 없음"));
        return ResponseEntity.ok(service.getSentSuggestions(sender));
    }

}