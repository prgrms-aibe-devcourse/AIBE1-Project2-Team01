package org.sunday.projectpop.project.model.service;

import lombok.RequiredArgsConstructor;
import org.sunday.projectpop.project.model.dto.ProjectRequest;
import org.sunday.projectpop.project.model.entity.*;
import org.sunday.projectpop.project.model.repository.ProjectRepository;
import org.sunday.projectpop.project.model.repository.ProjectRequireTagRepository;
import org.sunday.projectpop.project.model.repository.ProjectSelectiveTagRepository;
import org.sunday.projectpop.project.model.entity.UserAccount;
import org.springframework.stereotype.Service;

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
}
