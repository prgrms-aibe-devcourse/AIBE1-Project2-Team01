package org.sunday.projectpop.project.model.dto;

import lombok.Builder;
import org.sunday.projectpop.project.model.entity.Project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ProjectResponse(
        String projectId,
        String title,
        String description,
        String type,
        String status,
        boolean generatedByAi,
        String field,
        String experienceLevel,
        String locationType,
        int durationWeeks,
        int teamSize,
        LocalDateTime createdAt,
        String leaderEmail,
        List<String> requiredTags,
        List<String> selectiveTags
) {
    public static ProjectResponse from(Project project, List<String> requiredTags, List<String> selectiveTags) {
        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle())
                .description(project.getDescription())
                .type(project.getType())
                .status(project.getStatus())
                .generatedByAi(project.getGeneratedByAi())
                .field(project.getField())
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
}
