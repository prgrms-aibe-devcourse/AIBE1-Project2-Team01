package org.sunday.projectpop.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberContributionDto {
    private String memberName;
    private Long completedTasks;
    private Long AttendanceMeetings;
    private int contributionRate;
}
    
    