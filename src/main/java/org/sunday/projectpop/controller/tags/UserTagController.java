package org.sunday.projectpop.controller.tags;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.dto.tags.UserTagCacheDTO;
import org.sunday.projectpop.model.repository.UsersRepository;
import org.sunday.projectpop.service.tags.UserTagCacheService;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile/me/tags")
@RequiredArgsConstructor
@Tag(name = "User Tag", description = "Redis 기반 사용자 기술/성향 태그 API (본인 전용)")
public class UserTagController {

    private final UserTagCacheService tagCacheService;
    private final UsersRepository usersRepository;

    @Operation(summary = "내 태그 저장")
    @PostMapping
    public ResponseEntity<Void> saveTags(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody UserTagCacheDTO dto) {
        UUID userId = getUserId(userDetails);
        UserTagCacheDTO data = new UserTagCacheDTO(userId, dto.techTags(), dto.personalityScore(), dto.personalityText());
        tagCacheService.saveTags(data);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 태그 조회")
    @GetMapping
    public ResponseEntity<UserTagCacheDTO> getTags(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        return tagCacheService.getTags(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "내 태그 삭제")
    @DeleteMapping
    public ResponseEntity<Void> deleteTags(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        tagCacheService.deleteTags(userId);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserId(UserDetails userDetails) {
        return usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."))
                .getId();
    }
}
