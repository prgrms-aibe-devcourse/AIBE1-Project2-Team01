package org.sunday.projectpop.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecificationDto {
    private String id;
    private String onGoingProjectId;
    private String requirement;
    private String assignee;
    private String status;
    private String dueDate;      // 문자열로 포맷
    private Integer progressRate;
    private String createdAt;    // 문자열로 포맷
    private String updatedAt;
}
