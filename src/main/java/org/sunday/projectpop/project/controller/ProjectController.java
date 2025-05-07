package org.sunday.projectpop.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.sunday.projectpop.project.model.dto.ProjectRequest;
import org.sunday.projectpop.project.model.entity.UserAccount;
import org.sunday.projectpop.project.model.service.ProjectService;
import org.sunday.projectpop.project.model.service.SkillTagService;
import org.sunday.projectpop.project.model.entity.SkillTag;

import org.sunday.projectpop.project.model.dto.ProjectSearchCondition;
import org.sunday.projectpop.project.model.entity.Project;


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

    // 🖼️ 공고 작성 폼 (HTML 렌더링)
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("projectRequest", new ProjectRequest());
        model.addAttribute("tags", skillTagService.getAllTags());
        return "project/create"; // Thymeleaf 템플릿
    }

    // 📨 폼 제출용 공고 생성 (서버 렌더링용)
    @PostMapping("/submit")
    public String submitProject(@ModelAttribute ProjectRequest request) {
        String userId = "test-user-id";
        UserAccount leader = userAccountService.getUserById(userId);
        List<SkillTag> requiredTags = skillTagService.getTagsByIds(request.getRequiredTagIds());
        List<SkillTag> selectiveTags = skillTagService.getTagsByIds(request.getSelectiveTagIds());

        projectService.create(request, leader, requiredTags, selectiveTags);
        return "redirect:/projects/create?success";
    }

    // 🖼️ 공고 목록 화면 보여주기 (선택)
    @GetMapping
    public String listProjects(@ModelAttribute ProjectSearchCondition condition, Model model) {
        Page<Project> page = projectService.searchProjects(condition); // 페이징된 결과

        model.addAttribute("projects", page.getContent());      // 실제 공고 목록
        model.addAttribute("page", page);                       // 전체 페이지 정보
        model.addAttribute("condition", condition);             // 필터 상태 유지용

        return "project/list";
    }
}

