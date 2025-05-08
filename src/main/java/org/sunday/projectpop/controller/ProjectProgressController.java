package org.sunday.projectpop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.MemberContributionDto;
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
        System.out.println(">>>>> projectProgress = " + progress);  // 확인용 로그
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


    //프로젝트 기여도
    @GetMapping("/{onGoingProjectId}/contribution")
    public String contribution(@PathVariable Long onGoingProjectId, Model model) {
        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
        if (projectOpt.isEmpty()) {
            model.addAttribute("error", "존재하지 않는 프로젝트입니다.");
            return "error"; // 에러 페이지
        }

        // 프로젝트의 진행률 계산 (기존 로직 재사용)
        int projectProgress = specificationService.calculateProgressPercentage(onGoingProjectId);
        model.addAttribute("projectProgress", projectProgress);

        // 팀원별 기여도 계산 로직 (SpecificationService 에서 구현)
        List<MemberContributionDto> memberContributions = specificationService.calculateMemberContributions(onGoingProjectId);
        model.addAttribute("memberContributions", memberContributions);  // 모델에 추가

        model.addAttribute("onGoingProjectId", onGoingProjectId); // 템플릿에서 onGoingProjectId 사용 가능하도록 전달
        return "onproject/contribution"; // 뷰 이름 반환
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




}
