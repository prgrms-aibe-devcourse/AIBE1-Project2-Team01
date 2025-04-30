package org.sunday.projectpop.controller;

import org.springframework.ui.Model; // 올바른 import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.repository.SpecificationRepository;

@Controller
@RequestMapping("/projects/onprojects")
@RequiredArgsConstructor
public class ProjectProgressController {

    private final SpecificationRepository specificationRepository;

    @GetMapping("/")
    public String index(){
        return "onproject/index";
    }

    @GetMapping("/{onGoingProjectId}")
    public String getOnGoingProjectDetail(@PathVariable Long onGoingProjectId, Model model) {
        // projectId 값 확인
        System.out.println("선택한 프로젝트 ID: " + onGoingProjectId);
        int progress = calculateProgressPercentage(onGoingProjectId);
        model.addAttribute("projectProgress", progress);

        return "onproject/index";
    }

    @GetMapping("/test1")
    public String testProjectProgress(Model model) {
        Long onGoingProjectId = 3L;
        int progress = calculateProgressPercentage(onGoingProjectId);
        model.addAttribute("projectProgress", progress); // 변수명 소문자로 변경 권장
        return "onproject/index";
    }




    //프로젝트의 완료된 기능 카운트
    public long countCompletedSpecifications(Long projectId) {
        return 50;
//        return specificationRepository.countByOnGoingProjectIdAndStatus(projectId, "진행 완료");

    }

    //프로젝트의 명세서 카운트
    public long countAllSpecifications(Long projectId) {
        return 51;
//        return specificationRepository.count();
    }

    public int calculateProgressPercentage(Long projectId) {
        long completed = countCompletedSpecifications(projectId);
        long total = countAllSpecifications(projectId);

        if (total == 0) {
            return 0; // 0으로 나누지 않도록 주의
        }

        return (int) ((double) completed / total * 100);
    }
}
