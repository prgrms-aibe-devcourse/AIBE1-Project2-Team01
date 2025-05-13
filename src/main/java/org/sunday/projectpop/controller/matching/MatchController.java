package org.sunday.projectpop.controller.matching;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.MatchedUserDTO;
import org.sunday.projectpop.model.entity.Project;
import org.sunday.projectpop.model.entity.SkillTag;
import org.sunday.projectpop.model.entity.UserAccount;
import org.sunday.projectpop.model.entity.UserTrait;
import org.sunday.projectpop.model.repository.*;
import org.sunday.projectpop.service.matching.CompatibilityService;
import org.sunday.projectpop.service.matching.RedisTagService;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final ProjectRequireTagRepository projectRequireTagRepository;
    private final SkillTagRepository skillTagRepository;
    private final RedisTagService redisTagService;
    private final UserAccountRepository userRepo; // matchedUsers 조회용
    private final UserSkillTagRepository userSkillTagRepository;
    private final CompatibilityService compatibilityService;
    private final ProjectRepository projectRepository;
    private final UserTraitRepository userTraitRepository;

    // GET /match or /match?projectId=...
    @GetMapping
    public String getMatchPage(@RequestParam(required = false) String projectId, HttpSession session,
                               Model model) {


        // 프로젝트 기본 태그
        List<String> initialTags = projectId != null
                ? projectRequireTagRepository.findTagsByProjectId(projectId)
                .stream().map(SkillTag::getName).toList()
                : List.of();
        model.addAttribute("tags", initialTags);

        // 모든 태그 이름 (validTags 용)
        List<String> allNames = skillTagRepository.findAll()
                .stream().map(SkillTag::getName).toList();
        model.addAttribute("skillTagNames", allNames);

        // 리더의 OCEAN key 계산
        if (projectId != null) {
            Project project = projectRepository.findById(projectId).orElseThrow();
            String leaderId = project.getLeader().getUserId();
            UserTrait trait = userTraitRepository.findById(leaderId).orElseThrow();

            // 임시 로그인 유저 ID 저장
            String mockUserId = leaderId;  // 테스트용 senderId
            session.setAttribute("userId", mockUserId);
            model.addAttribute("userId", mockUserId);

            String leaderKey = "" +
                    trait.getOpenness() +
                    trait.getConscientiousness() +
                    trait.getExtraversion() +
                    trait.getAgreeableness() +
                    trait.getNeuroticism();
            model.addAttribute("leaderKey", leaderKey);
            model.addAttribute("projectId", projectId);
        } else {
            model.addAttribute("leaderKey", "");  // fallback
            model.addAttribute("projectId", "");
        }

        // 빈 결과
        model.addAttribute("matchedUsers", null);

        return "matching/index";
    }

    // POST /match/result
    @PostMapping("/result")
    public String postMatch(@RequestParam("tags") String tagCsv,
                            @RequestParam("projectId") String projectId,
                            HttpSession session,
                            Model model) {

        // 세션에서 다시 꺼내서 model에 넣어줘야 JS에서 쓸 수 있음
        String userId = (String) session.getAttribute("userId");
        model.addAttribute("userId", userId != null ? userId : "");
        // 1) 선택된 태그를 List<String>으로
        List<String> tagNames = List.of(tagCsv.split(","));
        model.addAttribute("tags", tagNames);

        // 2) 태그 이름 → 태그 ID
        List<Long> tagIds = skillTagRepository.findByNameIn(tagNames)
                .stream().map(SkillTag::getTagId).toList();

        // 3) Redis에서 매칭된 userId set
        Set<String> userIds = redisTagService.getRequireMatching(tagIds);

        // 4) 리더의 trait → leaderKey
        Project project = projectRepository.findById(projectId).orElseThrow();
        String leaderId = project.getLeader().getUserId();
        UserTrait trait = userTraitRepository.findById(leaderId).orElseThrow();
        String leaderKey = compatibilityService.makeLeaderOceanKey(trait);
        model.addAttribute("leaderKey", leaderKey);
        model.addAttribute("projectId", projectId);

        // 5) 궁합 점수 기반 정렬
        List<CompatibilityService.MatchedUserResult> sortedResults =
                compatibilityService.getSortedUserCompatibility(leaderId, List.copyOf(userIds));

        // 6) 정렬된 userId 기준으로 UserAccount 조회
        List<UserAccount> users = userRepo.findAllById(
                sortedResults.stream().map(CompatibilityService.MatchedUserResult::userId).toList()
        );

        // 지금은 점수 없이 넘기지만, 필요하면 DTO 조합 가능
        List<MatchedUserDTO> matchedUsers = sortedResults.stream()
                .map(result -> {
                    UserAccount user = users.stream()
                            .filter(u -> u.getUserId().equals(result.userId()))
                            .findFirst().orElseThrow();
                    return new MatchedUserDTO(user.getUserId(), user.getEmail(), result.compatibilityScore());
                })
                .toList();
        model.addAttribute("matchedUsers", matchedUsers);

        // 7) 태그 목록 유지
        List<String> allNames = skillTagRepository.findAll()
                .stream().map(SkillTag::getName).toList();
        model.addAttribute("skillTagNames", allNames);


        return "matching/index";
    }

    @GetMapping("/user-tags")
    @ResponseBody
    public List<String> getUserSkillTags(@RequestParam String userId) {
        return userSkillTagRepository.findTagNamesByUserId(userId); // 예시 메서드
    }

    @GetMapping("/user-personality")
    @ResponseBody
    public CompatibilityService.CompatibilityResult getUserPersonalityAndCompatibility(
            @RequestParam String userId,
            @RequestParam String leaderKey) {
        return compatibilityService.calculateCompatibility(userId, leaderKey);
    }




}
