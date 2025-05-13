package org.sunday.projectpop.model.dto;

import lombok.Data;
import java.util.List;

@Data
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
