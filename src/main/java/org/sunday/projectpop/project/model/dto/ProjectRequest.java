package org.sunday.projectpop.project.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectRequest {
    private String title;
    private String description;
    private String field;
    private String locationType;
    private Integer durationWeeks;
    private Integer teamSize;
//    private String requirement;
    private String type;
    private String experienceLevel;

    private List<Long> requiredTagIds;
    private List<Long> selectiveTagIds;
}
