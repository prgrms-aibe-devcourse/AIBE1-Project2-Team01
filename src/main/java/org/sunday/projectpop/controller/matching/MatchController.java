package org.sunday.projectpop.controller.matching;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sunday.projectpop.model.entity.SkillTag;
import org.sunday.projectpop.model.repository.ProjectRequireTagRepository;
import org.sunday.projectpop.model.repository.SkillTagRepository;
import org.sunday.projectpop.service.matching.RedisTagService;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class MatchController {
    private final ProjectRequireTagRepository projectRequireTagRepository;
    private final SkillTagRepository skillTagRepository;
    private final RedisTagService redisTagService;

    @GetMapping("/match")
    public String match(@RequestParam(required = false) String projectId, Model model) {
        List<SkillTag> tags = projectId != null
                ? projectRequireTagRepository.findTagsByProjectId(projectId)
                : List.of();
        model.addAttribute("tags", tags);
        model.addAttribute("skillTags", skillTagRepository.findAll());
        return "matching/index";
    }

    @PostMapping("/match/result")
    public String matchResult(@RequestParam("tags") String tagCsv, Model model) {
        List<String> tagNames = List.of(tagCsv.split(","));
        List<Long> tagIds = skillTagRepository.findByNameIn(tagNames).stream()
                .map(SkillTag::getTagId).toList();
        Set<String> matchedUserIds = redisTagService.getRequireMatching(tagIds);

        model.addAttribute("matchedUserIds", matchedUserIds);
        model.addAttribute("skillTags", skillTagRepository.findAll());
        model.addAttribute("tags", tagNames); // 태그도 다시 내려줘야 화면에 유지됨

        return "matching/index"; // 같은 화면에서 결과도 함께 보여줌
    }
}


