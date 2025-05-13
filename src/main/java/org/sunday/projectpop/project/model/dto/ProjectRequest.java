package org.sunday.projectpop.project.model.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC) // ✅ 추가
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ProjectRequest {
    private String title;
    private String description;
    private Long fieldId; // 프론트에서 fieldId 선택해서 넘김
    private String locationType;
    private Integer durationWeeks;
    private Integer teamSize;
//    private String requirement;
    private String type;
    private String experienceLevel;

    private List<Long> requiredTagIds;
    private List<Long> selectiveTagIds;
}
