package org.sunday.projectpop.project.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.sunday.projectpop.project.model.dto.ProjectRequest;
import org.sunday.projectpop.project.model.entity.*;
import org.sunday.projectpop.project.model.repository.ProjectRepository;
import org.sunday.projectpop.project.model.repository.ProjectRequireTagRepository;
import org.sunday.projectpop.project.model.repository.ProjectSelectiveTagRepository;
import org.sunday.projectpop.project.model.entity.UserAccount;
import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import org.sunday.projectpop.project.model.dto.ProjectSearchCondition;
import org.sunday.projectpop.project.model.repository.ProjectSpecification;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRequireTagRepository requireTagRepository;
    private final ProjectSelectiveTagRepository selectiveTagRepository;

    public Project create(ProjectRequest request, UserAccount leader, List<SkillTag> requiredTags, List<SkillTag> selectiveTags) {
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .locationType(request.getLocationType())
                .durationWeeks(request.getDurationWeeks())
                .teamSize(request.getTeamSize())
                .status("모집중")
                .generatedByAi(false)
                .createdAt(LocalDateTime.now())
                .leader(leader)

                .field(request.getField())
                .build();

        Project saved = projectRepository.save(project);


        System.out.println("✅ 저장된 프로젝트 ID: " + saved.getProjectId());
        System.out.println("✅ 제목: " + saved.getTitle());
        System.out.println("✅ 팀장 ID: " + saved.getLeader().getUserId());


        requiredTags.forEach(tag -> requireTagRepository.save(
                ProjectRequireTag.builder().project(saved).tag(tag).build()
        ));

        selectiveTags.forEach(tag -> selectiveTagRepository.save(
                ProjectSelectiveTag.builder().project(saved).tag(tag).build()
        ));

        return saved;
    }
    //  조건 기반 공고 목록 조회
    public Page<Project> searchProjects(ProjectSearchCondition condition,Pageable pageable) {
        System.out.println("🔍 검색 조건 status: " + condition.getStatus()); // 리스트로 잘 들어오는지
        System.out.println("🔍 검색 조건 sortBy: " + condition.getSortBy());

        pageable = PageRequest.of(
                condition.getPage(),
                condition.getSize(),
                "최신순".equalsIgnoreCase(condition.getSortBy()) ? Sort.by("createdAt").descending() : Sort.by("createdAt").ascending()
        );

       // return projectRepository.findAll(ProjectSpecification.search(condition), pageable);

        Page<Project> result = projectRepository.findAll(ProjectSpecification.search(condition), pageable);

        System.out.println("✅ 전체 저장된 프로젝트 수: " + projectRepository.count());
        System.out.println("✅ 검색 결과 총 개수: " + result.getTotalElements());
        System.out.println("✅ 현재 페이지 콘텐츠 수: " + result.getContent().size());

        result.getContent().forEach(p -> {
            System.out.println("▶ 프로젝트 제목: " + p.getTitle());
            System.out.println("▶ 상태: " + p.getStatus());
            System.out.println("▶ 생성일: " + p.getCreatedAt());
            System.out.println("🆔 ID: " + p.getProjectId());
        });

        return result;
    }




}
