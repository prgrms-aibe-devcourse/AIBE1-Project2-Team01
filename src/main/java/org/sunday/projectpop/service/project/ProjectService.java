package org.sunday.projectpop.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.sunday.projectpop.model.entity.ProjectField;
import org.sunday.projectpop.model.repository.ProjectFieldRepository;
import org.sunday.projectpop.model.repository.ProjectSelectiveTagRepository;
import org.sunday.projectpop.model.repository.ProjectSpecification;
import org.sunday.projectpop.model.dto.ProjectRequest;
import org.sunday.projectpop.model.dto.ProjectResponse;
import org.sunday.projectpop.project.model.entity.*;
import org.springframework.stereotype.Service;

import org.sunday.projectpop.model.dto.ProjectSearchCondition;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRequireTagRepository requireTagRepository;
    private final ProjectSelectiveTagRepository selectiveTagRepository;
    private final ProjectFieldRepository projectFieldRepository;


    public Project create(ProjectRequest request, UserAccount leader, List<SkillTag> requiredTags, List<SkillTag> selectiveTags) {

        ProjectField field = projectFieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new IllegalArgumentException("❌ 유효하지 않은 분야 ID입니다."));
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
                .experienceLevel(request.getExperienceLevel())
                .leader(leader)

                .field(field)
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

    public ProjectResponse getProjectDetailWithTags(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

        List<String> requiredTags = getRequiredTagNames(projectId);
        List<String> selectiveTags = getSelectiveTagNames(projectId);

        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle())
                .description(project.getDescription())
                .type(project.getType())
                .status(project.getStatus())
                .generatedByAi(project.getGeneratedByAi())
                .field(project.getField().getName())
                .experienceLevel(project.getExperienceLevel())
                .locationType(project.getLocationType())
                .durationWeeks(project.getDurationWeeks())
                .teamSize(project.getTeamSize())
                .createdAt(project.getCreatedAt())
                .leaderEmail(project.getLeader().getEmail())
                .requiredTags(requiredTags)
                .selectiveTags(selectiveTags)
                .build();
    }
    public List<String> getRequiredTagNames(String projectId) {
        return requireTagRepository.findByProject_ProjectId(projectId).stream()
                .map(rt -> rt.getTag().getName())
                .collect(Collectors.toList());
    }

    public List<String> getSelectiveTagNames(String projectId) {
        return selectiveTagRepository.findByProject_ProjectId(projectId).stream()
                .map(st -> st.getTag().getName())
                .collect(Collectors.toList());
    }



}
