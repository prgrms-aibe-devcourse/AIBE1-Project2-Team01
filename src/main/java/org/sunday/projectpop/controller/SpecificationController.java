package org.sunday.projectpop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.OnGoingProject;
import org.sunday.projectpop.model.entity.Specification;
import org.sunday.projectpop.service.ongoingproject.OnGoingProjectService;
import org.sunday.projectpop.service.specification.SpecificationService;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/projects/onprojects")
@RequiredArgsConstructor
public class SpecificationController {
    private final OnGoingProjectService onGoingProjectService;
    private final SpecificationService specificationService;

    // 명세서 수정 폼 요청 처리
    @GetMapping("/{onGoingProjectId}/specs/edit/{specId}")
    public String editSpecificationForm(@PathVariable String onGoingProjectId, @PathVariable String specId, Model model) {
        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
        Optional<Specification> specificationOpt = specificationService.findById(specId);

        if (projectOpt.isEmpty() || specificationOpt.isEmpty()) {
            model.addAttribute("error", "프로젝트 또는 명세서를 찾을 수 없습니다.");
            return "error";
        }

        model.addAttribute("onGoingProjectId", onGoingProjectId);
        model.addAttribute("specification", specificationService.convertToDto(specificationOpt.get()));
        return "onproject/specification/editspecification"; // 수정 폼 템플릿
    }

    @PostMapping("/{onGoingProjectId}/specs/edit/{specId}")
    public String updateSpecification(@PathVariable String onGoingProjectId, @PathVariable String specId, @ModelAttribute SpecificationDto specificationDto, Model model) {
        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
        if (projectOpt.isEmpty()) {
            model.addAttribute("error", "존재하지 않는 프로젝트입니다.");
            return "error";
        }

        specificationDto.setOnGoingProjectId(onGoingProjectId); // DTO에 프로젝트 ID 설정
        specificationService.updateSpecification(specId, specificationDto);
        return "redirect:/projects/onprojects/" + onGoingProjectId;
    }

    // 명세서 추가 폼 요청 처리
    @GetMapping("/{onGoingProjectId}/specs/new")
    public String addSpecificationForm(@PathVariable String onGoingProjectId, Model model) {
        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);

        if (projectOpt.isEmpty()) {
            model.addAttribute("error", "존재하지 않는 프로젝트입니다.");
            return "error";
        }

        model.addAttribute("onGoingProjectId", onGoingProjectId);
        model.addAttribute("specificationDto", new SpecificationDto()); // 빈 DTO 추가
        return "onproject/specification/newspecification"; // 명세서 추가 폼 템플릿
    }

    @PostMapping("/{onGoingProjectId}/specs/add")
    public String addSpecification(@PathVariable String onGoingProjectId, @ModelAttribute SpecificationDto specificationDto, Model model) {
        Specification specification = new Specification();

        // OnGoingProject 엔티티를 찾아서 설정
        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
        if (projectOpt.isEmpty()) {
            model.addAttribute("error", "존재하지 않는 프로젝트입니다.");
            return "error";
        }
        OnGoingProject onGoingProject = projectOpt.get();
        specification.setOnGoingProject(onGoingProject);

        specification.setRequirement(specificationDto.getRequirement());
        specification.setAssignee(specificationDto.getAssignee());
        specification.setStatus(specificationDto.getStatus());

        // 날짜 파싱 예외 처리
        try {
            specification.setDueDate(LocalDate.parse(specificationDto.getDueDate()));
        } catch (Exception e) {
            model.addAttribute("error", "잘못된 날짜 형식입니다.");
            return "onproject/specifications";
        }


        specificationService.save(specification);

        return "redirect:/projects/onprojects/" + onGoingProjectId;
    }

    @PostMapping("/{onGoingProjectId}/specs/delete/{specId}")
    public String deleteSpecification(@PathVariable String onGoingProjectId, @PathVariable String specId) {
        Optional<OnGoingProject> projectOpt = onGoingProjectService.findById(onGoingProjectId);
        Optional<Specification> specificationOpt = specificationService.findById(specId);

        if (projectOpt.isEmpty() || specificationOpt.isEmpty()) {
            // 프로젝트 또는 명세서가 존재하지 않으면 에러 처리 (여기서는 간단히 리다이렉트)
            return "redirect:/projects/onprojects/" + onGoingProjectId + "?error=notfound";
        }

        specificationService.delete(specId);
        return "redirect:/projects/onprojects/" + onGoingProjectId;
    }
}
