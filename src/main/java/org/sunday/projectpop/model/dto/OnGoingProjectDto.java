package org.sunday.projectpop.model.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OnGoingProjectDto {
    private String onGoingProjectId;
    private String projectId;
    private String teamLeaderId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
