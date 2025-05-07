package org.sunday.projectpop.controller.matching;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sunday.projectpop.model.entity.SkillTag;
import org.sunday.projectpop.model.entity.UserAccount;
import org.sunday.projectpop.model.repository.ProjectRequireTagRepository;
import org.sunday.projectpop.model.repository.SkillTagRepository;
import org.sunday.projectpop.model.repository.UserAccountRepository;
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

    // GET /match or /match?projectId=...
    @GetMapping
    public String getMatchPage(@RequestParam(required = false) String projectId,
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

        // 빈 결과
        model.addAttribute("matchedUsers", null);

        return "matching/index";
    }

    // POST /match/result
    @PostMapping("/result")
    public String postMatch(@RequestParam("tags") String tagCsv,
                            Model model) {
        // 1) 선택된 태그를 List<String>으로
        List<String> tagNames = List.of(tagCsv.split(","));
        model.addAttribute("tags", tagNames);

        // 2) 태그 이름 → 태그 ID
        List<Long> tagIds = skillTagRepository.findByNameIn(tagNames)
                .stream().map(SkillTag::getTagId).toList();

        // 3) Redis에서 매칭된 userId set
        Set<String> userIds = redisTagService.getRequireMatching(tagIds);

        // 4) userId → UserAccount 엔티티 리스트
        List<UserAccount> matched = userRepo.findAllById(userIds);
        model.addAttribute("matchedUsers", matched);

        // 5) validTags 유지
        List<String> allNames = skillTagRepository.findAll()
                .stream().map(SkillTag::getName).toList();
        model.addAttribute("skillTagNames", allNames);

        return "matching/index";
    }
}
