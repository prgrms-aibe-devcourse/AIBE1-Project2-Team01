package org.sunday.projectpop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.OnGoingProject;
import org.sunday.projectpop.model.entity.Specification;
import org.sunday.projectpop.service.OnGoingProjectService;
import org.sunday.projectpop.service.SpecificationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/projects/onprojects")
@RequiredArgsConstructor
public class ProjectProgressController {
    private final OnGoingProjectService onGoingProjectService;
    private final SpecificationService specificationService;

    @GetMapping("/")
    public String index() {
        return "onproject/index";
    }
    //프로젝트 기본 페이지
    @GetMapping("/{onGoingProjectId}")
    public String getOnGoingProjectDetail(@PathVariable Long onGoingProjectId, Model model) {
        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
        if (projectOpt.isEmpty()) {
            model.addAttribute("error", "존재하지 않는 프로젝트입니다.");
            return "error"; // 에러 페이지
        }
        // 프로젝트 진행 상태 계산
        int progress = specificationService.calculateProgressPercentage(onGoingProjectId);
        model.addAttribute("projectProgress", progress);

        return "onproject/index";
    }
    //프로젝트의 명세서 출력
    @GetMapping("/{onGoingProjectId}/specs")
    public String getSpecificationsByProject(@PathVariable Long onGoingProjectId, Model model) {
        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
        if (projectOpt.isEmpty()) {
            model.addAttribute("error", "존재하지 않는 프로젝트입니다.");
            return "error"; // 에러 페이지
        }
        // 프로젝트 ID에 해당하는 명세서 리스트 가져오기
        List<SpecificationDto> specifications = specificationService.getSpecificationsDtoByProjectId(onGoingProjectId);
        model.addAttribute("specList", specifications);

        return "onproject/specifications";  // Thymeleaf 템플릿명
    }

    @GetMapping("/test1")
    public String testProjectProgress(Model model) {
        try {
            int progress = specificationService.calculateProgressPercentage(3L);
            model.addAttribute("projectProgress", progress);
        } catch (Exception e) {
            e.printStackTrace(); // 로그에 출력
            model.addAttribute("projectProgress", 0); // fallback
        }
        return "onproject/index";
    }

    @PostMapping("/{onGoingProjectId}/specs/add")
    public String addSpecification(@PathVariable Long onGoingProjectId, @ModelAttribute SpecificationDto specificationDto, Model model) {
        Specification specification = new Specification();

        // OnGoingProject 엔티티를 찾아서 설정
        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
        if (projectOpt.isEmpty()) {
            model.addAttribute("error", "존재하지 않는 프로젝트입니다.");
            return "error";
        }
        OnGoingProject onGoingProject = projectOpt.get();
        specification.setOnGoingProject(onGoingProject); // OnGoingProject 객체 설정

        specification.setRequirement(specificationDto.getRequirement());
        specification.setAssignee(specificationDto.getAssignee());
        specification.setStatus(specificationDto.getStatus());

        // 날짜 파싱 예외 처리
        try {
            specification.setDueDate(LocalDate.parse(specificationDto.getDueDate()));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "잘못된 날짜 형식입니다.");
            return "onproject/specifications";
        }

        specification.setProgressRate(specificationDto.getProgressRate());

        specificationService.save(specification);

        // 명세서 목록 페이지로 리다이렉트
        return "redirect:/projects/onprojects/" + onGoingProjectId + "/specs";
    }


    @GetMapping("/all")
    public String getAllOnGoingProjects() {
        List<OnGoingProject> projects = onGoingProjectService.findAll();  // 모든 프로젝트 조회

        // System.out.println()으로 프로젝트 출력
        for (OnGoingProject project : projects) {
            System.out.println("Project ID: " + project.getProjectId() +
                    ", Team Leader: " + project.getTeamLeaderId() +
                    ", Status: " + project.getStatus() +
                    ", Start Date: " + project.getStartDate() +
                    ", End Date: " + project.getEndDate());
        }

        return "onproject/index";  // 예시로 index 페이지 반환
    }

}
