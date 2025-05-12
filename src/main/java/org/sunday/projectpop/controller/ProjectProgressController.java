package org.sunday.projectpop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.MemberContributionDto;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.OnGoingProject;
import org.sunday.projectpop.service.healthcheck.HealthCheckService;
import org.sunday.projectpop.service.ongoingproject.OnGoingProjectService;
import org.sunday.projectpop.service.specification.SpecificationService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/projects/onprojects")
@RequiredArgsConstructor
public class ProjectProgressController {
    private final OnGoingProjectService onGoingProjectService;
    private final SpecificationService specificationService;
    private final HealthCheckService healthCheckService;

    @GetMapping("/")
    public String index() {
        return "onproject/index";
    }

    // 프로젝트 기본 페이지
    @GetMapping("/{onGoingProjectId}")
    public String getProjectDashboard(@PathVariable String onGoingProjectId, Model model) {
        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
        if (projectOpt.isEmpty()) {
            model.addAttribute("error", "존재하지 않는 프로젝트입니다.");
            return "error";
        }

        // 프로젝트 진행률 계산
        int projectProgress = specificationService.calculateProgressPercentage(onGoingProjectId);
        model.addAttribute("projectProgress", projectProgress);

        // 팀원별 기여도 계산 로직
        List<MemberContributionDto> memberContributions = specificationService.calculateMemberContributions(onGoingProjectId);
        model.addAttribute("memberContributions", memberContributions);

        // 명세서 리스트
        List<SpecificationDto> specifications = specificationService.getSpecificationsDtoByProjectId(onGoingProjectId);
        model.addAttribute("specList", specifications);

        // 일정 준수율 계산
        int scheduleCompliance = healthCheckService.calculateScheduleCompliance(specifications);
        model.addAttribute("scheduleCompliance", scheduleCompliance);

        // 리스크 현황 계산
        int riskStatus = healthCheckService.calculateRiskStatus(specifications);
        model.addAttribute("riskStatus", riskStatus);

        // 프로젝트 ID 전달 (템플릿에서 사용)
        model.addAttribute("onGoingProjectId", onGoingProjectId);

        return "onproject/index";
    }

//    // 명세서 수정 폼 요청 처리
//    @GetMapping("/{onGoingProjectId}/specs/edit/{specId}")
//    public String editSpecificationForm(@PathVariable Long onGoingProjectId, @PathVariable Long specId, Model model) {
//        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
//        Optional<Specification> specificationOpt = specificationService.findById(specId);
//
//        if (projectOpt.isEmpty() || specificationOpt.isEmpty()) {
//            model.addAttribute("error", "프로젝트 또는 명세서를 찾을 수 없습니다.");
//            return "error";
//        }
//
//        model.addAttribute("onGoingProjectId", onGoingProjectId);
//        model.addAttribute("specification", specificationService.convertToDto(specificationOpt.get()));
//        return "onproject/editSpecification"; // 수정 폼 템플릿
//    }
//
//    @PostMapping("/{onGoingProjectId}/specs/edit/{specId}")
//    public String updateSpecification(@PathVariable Long onGoingProjectId, @PathVariable Long specId, @ModelAttribute SpecificationDto specificationDto, Model model) {
//        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
//        if (projectOpt.isEmpty()) {
//            model.addAttribute("error", "존재하지 않는 프로젝트입니다.");
//            return "error";
//        }
//
//        specificationDto.setOnGoingProjectId(onGoingProjectId); // DTO에 프로젝트 ID 설정
//        specificationService.updateSpecification(specId, specificationDto);
//        return "redirect:/projects/onprojects/" + onGoingProjectId;
//    }


}