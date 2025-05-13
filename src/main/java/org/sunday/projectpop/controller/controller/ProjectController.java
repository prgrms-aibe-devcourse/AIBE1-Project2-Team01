package org.sunday.projectpop.controller.controller;

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
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final SkillTagService skillTagService;
    private final UserAccountService userAccountService;
    private final ProjectApplicationService applicationService;
    private final ProjectFieldService projectFieldService;




    // 🖼️ 공고 작성 폼 (HTML 렌더링)
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("projectRequest", new ProjectRequest());
        model.addAttribute("tags", skillTagService.getAllTags());
        model.addAttribute("fields", projectFieldService.getAllFields()); // 💡 추가

        System.out.println("✅ 등록된 태그: " + skillTagService.getAllTags());

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
        return "redirect:/projects";
    }

    // 🖼️ 공고 목록 화면 보여주기 (선택)
//    @GetMapping
//    public String listProjects(@ModelAttribute ProjectSearchCondition condition, Model model) {
//        Page<Project> page = projectService.searchProjects(condition); // 페이징된 결과
//
//        model.addAttribute("projects", page.getContent());      // 실제 공고 목록
//        model.addAttribute("page", page);                       // 전체 페이지 정보
//        model.addAttribute("condition", condition);             // 필터 상태 유지용
//
//        return "project/list";
//    }
    @GetMapping
    public String listProjects(
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

//    @GetMapping("/filter")
//    public String filterProjectsAjax(@ModelAttribute ProjectSearchCondition condition,
//                                     @PageableDefault(size = 6) Pageable pageable,
//                                     Model model) {
//        Page<Project> projects = projectService.searchProjects(condition, pageable);
//        model.addAttribute("projects", projects.getContent());
//        return "project/list :: projectList"; // fragment만 반환
//    }
@GetMapping("/filter")
public String filterProjectsAjax(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String sortBy,
        @PageableDefault(size = 6) Pageable pageable,
        Model model
) {
    ProjectSearchCondition condition = new ProjectSearchCondition();
    if (status != null) condition.setStatus(List.of(status));
    if (sortBy != null) condition.setSortBy(sortBy);

    Page<Project> projects = projectService.searchProjects(condition, pageable);

    List<ProjectResponse> responseList = projects.getContent().stream()
            .map(project -> ProjectResponse.from(
                    project,
                    projectService.getRequiredTagNames(project.getProjectId()),
                    projectService.getSelectiveTagNames(project.getProjectId())
            ))
            .toList();

    model.addAttribute("projects", responseList); // ✅ 필터도 DTO로
    return "project/list :: projectList";
}


//    @GetMapping("/{projectId}")
//    public String viewProjectDetail(@PathVariable String projectId, Model model) {
//        ProjectResponse response = projectService.getProjectDetailWithTags(projectId);
//        List<String> requiredTags = projectService.getRequiredTagNames(projectId);
//        List<String> selectiveTags = projectService.getSelectiveTagNames(projectId);
//
//        model.addAttribute("project", response);
//        model.addAttribute("requiredTags", requiredTags);
//        model.addAttribute("selectiveTags", selectiveTags);
//        return "project/details"; // Thymeleaf 템플릿
//    }
@GetMapping("/detail")
public String viewProjectDetail(@RequestParam("projectId") String projectId, Model model) {
    ProjectResponse response = projectService.getProjectDetailWithTags(projectId);
    List<String> requiredTags = projectService.getRequiredTagNames(projectId);
    List<String> selectiveTags = projectService.getSelectiveTagNames(projectId);

    model.addAttribute("project", response);
    model.addAttribute("requiredTags", requiredTags);
    model.addAttribute("selectiveTags", selectiveTags);
    return "project/details";
}

   @PostMapping("/apply")
    public String apply(@RequestParam("projectId") String projectId,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        try {
            applicationService.applyToProject(projectId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "지원이 완료되었습니다!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

       return "redirect:/projects/detail?projectId=" + projectId;

   }


}

