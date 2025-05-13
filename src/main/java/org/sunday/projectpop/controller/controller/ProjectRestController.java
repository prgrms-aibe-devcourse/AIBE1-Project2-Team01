//package org.sunday.projectpop.project.controller;
//
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.sunday.projectpop.model.dto.ProjectRequest;
//import org.sunday.projectpop.model.dto.ProjectResponse;
//import org.sunday.projectpop.model.dto.ProjectSearchCondition;
//import org.sunday.projectpop.project.model.entity.Project;
//import org.sunday.projectpop.project.model.entity.SkillTag;
//import org.sunday.projectpop.project.model.entity.UserAccount;
//import org.sunday.projectpop.project.model.service.ProjectService;
//import org.sunday.projectpop.project.model.service.SkillTagService;
//import org.sunday.projectpop.project.model.service.UserAccountService;
//import org.springframework.data.domain.Pageable;
//
//
//import java.util.List;
//
//
//    @Tag(name = "프로젝트", description = "프로젝트 공고 관련 API")
//    @RestController
//    @RequestMapping("/api/projects")
//    @RequiredArgsConstructor
//    public class ProjectRestController {
//
//        private final ProjectService projectService;
//        private final SkillTagService skillTagService;
//        private final UserAccountService userAccountService;
//
////        // 🔍 공고 목록 조회 API (JSON)
////        @GetMapping
////        public Page<ProjectResponse> listProjects(@ModelAttribute ProjectSearchCondition condition,Pageable pageable) {
////            return projectService.searchProjects(condition,pageable)
////                    .map(ProjectResponse::from);
////        }
//
//        // 📨 공고 생성 API (JSON)
//        @PostMapping
//        public ResponseEntity<Project> createProject(@RequestBody ProjectRequest request) {
//            String userId = "test-user-id"; // 테스트용
//            UserAccount leader = userAccountService.getUserById(userId);
//            List<SkillTag> requiredTags = skillTagService.getTagsByIds(request.getRequiredTagIds());
//            List<SkillTag> selectiveTags = skillTagService.getTagsByIds(request.getSelectiveTagIds());
//
//            Project created = projectService.create(request, leader, requiredTags, selectiveTags);
//            return ResponseEntity.ok(created);
//        }
//        @GetMapping("/{projectId}")
//        public ResponseEntity<ProjectResponse> getProjectDetail(@PathVariable String projectId) {
//            ProjectResponse response = projectService.getProjectDetailWithTags(projectId);
//            return ResponseEntity.ok(response);
//        }
//    }
//
