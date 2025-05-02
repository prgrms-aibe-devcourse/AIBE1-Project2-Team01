package org.sunday.projectpop.project.controller;

import lombok.RequiredArgsConstructor;
import org.sunday.projectpop.project.model.dto.ProjectRequest;
import org.sunday.projectpop.project.model.entity.UserAccount;
import org.sunday.projectpop.project.model.service.ProjectService;
import org.sunday.projectpop.project.model.service.SkillTagService;
import org.sunday.projectpop.project.model.entity.SkillTag;

import org.sunday.projectpop.project.model.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final SkillTagService skillTagService;
    private final UserAccountService userAccountService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("projectRequest", new ProjectRequest());
        model.addAttribute("tags", skillTagService.getAllTags());
        return "project/create";
    }

    @PostMapping
    public ResponseEntity<Void> createProject(@RequestBody ProjectRequest request) {
        String userId = "test-user-id";
        UserAccount leader = userAccountService.getUserById(userId); // 외부에서 유저 조회
        List<SkillTag> requiredTags = skillTagService.getTagsByIds(request.getRequiredTagIds());
        List<SkillTag> selectiveTags = skillTagService.getTagsByIds(request.getSelectiveTagIds());

        projectService.create(request, leader, requiredTags, selectiveTags);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/submit")
    //  @AuthenticationPrincipal(expression = "userId") String userId
    public String submitProject(@ModelAttribute ProjectRequest request) {
        String userId = "test-user-id";
        UserAccount leader = userAccountService.getUserById(userId);
        List<SkillTag> requiredTags = skillTagService.getTagsByIds(request.getRequiredTagIds());
        List<SkillTag> selectiveTags = skillTagService.getTagsByIds(request.getSelectiveTagIds());

        projectService.create(request, leader, requiredTags, selectiveTags);
        return "redirect:/projects/create?success";
    }
}
