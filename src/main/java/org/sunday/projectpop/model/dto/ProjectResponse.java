package org.sunday.projectpop.model.dto;

import lombok.Builder;
import org.sunday.projectpop.model.entity.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        List<String> selectiveTags,

        int applicantCount,// 지원자 수
        LocalDate deadline// 마감일 (createdAt + durationWeeks 계산)
) {
    public static ProjectResponse from(Project project, List<String> requiredTags, List<String> selectiveTags) {

        String status = calculateStatus(project.getCreatedAt(), project.getDurationWeeks());
        LocalDate deadline = project.getCreatedAt().toLocalDate().plusWeeks(project.getDurationWeeks());
        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle())
                .description(project.getDescription())
                .type(project.getType())
                .status(status)
                .generatedByAi(project.getGeneratedByAi())
                .field(project.getField().getDescription())
                .experienceLevel(project.getExperienceLevel())
                .locationType(project.getLocationType())
                .durationWeeks(project.getDurationWeeks())
                .teamSize(project.getTeamSize())
                .createdAt(project.getCreatedAt())
                .leaderEmail(project.getLeader().getEmail())
                .requiredTags(requiredTags)
                .selectiveTags(selectiveTags)
                .deadline(deadline)
//                .applicantCount(applicantCount)

                .build();
    }
    private static String calculateStatus(LocalDateTime createdAt, int durationWeeks) {
        LocalDateTime endDate = createdAt.plusWeeks(durationWeeks);
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(createdAt)) return "모집중";
        else if (now.isAfter(endDate)) return "완료";
        else return "진행중";
    }
}
