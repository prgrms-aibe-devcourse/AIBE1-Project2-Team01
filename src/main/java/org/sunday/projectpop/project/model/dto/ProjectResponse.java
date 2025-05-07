package org.sunday.projectpop.project.model.dto;

import lombok.Builder;
import org.sunday.projectpop.project.model.entity.Project;

import java.time.LocalDateTime;

@Builder
public record ProjectResponse(
        String projectId,
        String title,
        String description,
        String type,
        String status,
        boolean generatedByAi,
        String field,
        String locationType,
        int durationWeeks,
        int teamSize,
        LocalDateTime createdAt,
        String leaderEmail
) {
    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .projectId(project.getProjectId().toString())
                .title(project.getTitle())
                .description(project.getDescription())
                .type(project.getType())
                .status(project.getStatus())
                .generatedByAi(project.getGeneratedByAi())
                .field(project.getField())
                .locationType(project.getLocationType())
                .durationWeeks(project.getDurationWeeks())
                .teamSize(project.getTeamSize())
                .createdAt(project.getCreatedAt())
                .leaderEmail(project.getLeader().getEmail())
                .build();
    }
}
