package org.sunday.projectpop.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpecificationDto {
    private Long id;
    private Long onGoingProjectId;
    private String requirement;
    private String assignee;
    private String status;
    private String dueDate;      // 문자열로 포맷
    private Integer progressRate;
    private String createdAt;    // 문자열로 포맷
    private String updatedAt;
}
