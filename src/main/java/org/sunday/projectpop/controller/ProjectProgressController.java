package org.sunday.projectpop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.service.SpecificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/projects/onprojects")
@RequiredArgsConstructor
public class ProjectProgressController {

    private final SpecificationService specificationService;  // 자동 주입

    @GetMapping("/")
    public String index() {
        return "onproject/index";
    }

    @GetMapping("/{onGoingProjectId}")
    public String getOnGoingProjectDetail(@PathVariable Long onGoingProjectId, Model model) {
        // 프로젝트 진행 상태 계산
        int progress = specificationService.calculateProgressPercentage(onGoingProjectId);
        model.addAttribute("projectProgress", progress);

        return "onproject/index";
    }

    @GetMapping("/{onGoingProjectId}/specs")
    public String getSpecificationsByProject(@PathVariable Long onGoingProjectId, Model model) {
        // 프로젝트 ID에 해당하는 명세서 리스트 가져오기
        List<SpecificationDto> specifications = specificationService.getSpecificationsDtoByProjectId(onGoingProjectId);
        model.addAttribute("specList", specifications);

        return "onproject/specifications";  // Thymeleaf 템플릿명
    }

    @GetMapping("/testspecs")
    public String testSpecificationsView(Model model) {
        // 샘플 데이터 추가 (일반적으로 실제 DB에서 가져오는 방식으로 변경)
        List<SpecificationDto> mockSpecs = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        mockSpecs.add(new SpecificationDto(1L, "Login 기능 구현", "홍길동", "진행중",
                LocalDate.of(2025, 5, 20).format(dateFormatter), 60,
                LocalDateTime.now().minusDays(5).format(dateTimeFormatter),
                LocalDateTime.now().format(dateTimeFormatter)));

        mockSpecs.add(new SpecificationDto(2L, "결제 시스템 연결", "이몽룡", "완료",
                LocalDate.of(2025, 5, 10).format(dateFormatter), 100,
                LocalDateTime.now().minusDays(10).format(dateTimeFormatter),
                LocalDateTime.now().format(dateTimeFormatter)));

        mockSpecs.add(new SpecificationDto(3L, "UI 디자인 정리", "성춘향", "대기중",
                LocalDate.of(2025, 5, 30).format(dateFormatter), 0,
                LocalDateTime.now().minusDays(1).format(dateTimeFormatter),
                LocalDateTime.now().format(dateTimeFormatter)));

        model.addAttribute("specifications", mockSpecs);
        return "onproject/specifications";
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
}
