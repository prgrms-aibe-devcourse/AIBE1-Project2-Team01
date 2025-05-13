package org.sunday.projectpop.controller.main;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.sunday.projectpop.model.dto.ProjectRequest;
import org.sunday.projectpop.model.dto.ProjectResponse;

import org.sunday.projectpop.model.dto.ProjectSearchCondition;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.entity.Project;
import org.sunday.projectpop.model.entity.SkillTag;
import org.sunday.projectpop.model.entity.UserAccount;
import org.sunday.projectpop.service.project.*;


import java.util.List;
@Controller
@RequiredArgsConstructor
public class MainController {

    private final ProjectService projectService;
    private final SkillTagService skillTagService;
    private final UserAccountService userAccountService;
    private final ProjectApplicationService applicationService;
    private final ProjectFieldService projectFieldService;


    // 🖼️ 공고 작성 폼 (HTML 렌더링)
    @GetMapping("/")
    public String mainPage(
            @ModelAttribute ProjectSearchCondition condition,
            @PageableDefault(size = 6) Pageable pageable,
            Model model
    ) {
        Page<Project> projects = projectService.searchProjects(condition, pageable);

        List<ProjectResponse> responseList = projects.getContent().stream()
                .map(project -> ProjectResponse.from(
                        project,
                        projectService.getRequiredTagNames(project.getProjectId()),
                        projectService.getSelectiveTagNames(project.getProjectId())
                ))
                .toList();

        model.addAttribute("projects", responseList);
        model.addAttribute("condition", condition);
        model.addAttribute("tags", skillTagService.getAllTags());
        model.addAttribute("fields", projectFieldService.getAllFields());

        return "project/list_new";
    }
}

