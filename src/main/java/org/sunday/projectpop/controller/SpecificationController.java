package org.sunday.projectpop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.OnGoingProject;
import org.sunday.projectpop.model.entity.Specification;
import org.sunday.projectpop.service.OnGoingProjectService;
import org.sunday.projectpop.service.SpecificationService;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/projects/onprojects")
@RequiredArgsConstructor
public class SpecificationController {
    private final OnGoingProjectService onGoingProjectService;
    private final SpecificationService specificationService;

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
        return "redirect:/projects/onprojects/" + onGoingProjectId;
    }
}
