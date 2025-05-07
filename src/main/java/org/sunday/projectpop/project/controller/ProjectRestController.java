package org.sunday.projectpop.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.project.model.dto.ProjectRequest;
import org.sunday.projectpop.project.model.dto.ProjectResponse;
import org.sunday.projectpop.project.model.dto.ProjectSearchCondition;
import org.sunday.projectpop.project.model.entity.Project;
import org.sunday.projectpop.project.model.entity.SkillTag;
import org.sunday.projectpop.project.model.entity.UserAccount;
import org.sunday.projectpop.project.model.service.ProjectService;
import org.sunday.projectpop.project.model.service.SkillTagService;
import org.sunday.projectpop.project.model.service.UserAccountService;

import java.util.List;


    @RestController
    @RequestMapping("/api/projects")
    @RequiredArgsConstructor
    public class ProjectRestController {

        private final ProjectService projectService;
        private final SkillTagService skillTagService;
        private final UserAccountService userAccountService;

        // 🔍 공고 목록 조회 API (JSON)
        @GetMapping
        public Page<ProjectResponse> listProjects(@ModelAttribute ProjectSearchCondition condition) {
            return projectService.searchProjects(condition)
                    .map(ProjectResponse::from);
        }

        // 📨 공고 생성 API (JSON)
        @PostMapping
        public ResponseEntity<Project> createProject(@RequestBody ProjectRequest request) {
            String userId = "test-user-id"; // 테스트용
            UserAccount leader = userAccountService.getUserById(userId);
            List<SkillTag> requiredTags = skillTagService.getTagsByIds(request.getRequiredTagIds());
            List<SkillTag> selectiveTags = skillTagService.getTagsByIds(request.getSelectiveTagIds());

            Project created = projectService.create(request, leader, requiredTags, selectiveTags);
            return ResponseEntity.ok(created);
        }
    }

