package org.sunday.projectpop.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberContributionDto {
    private String memberName;
    private Long completedTasks;
    private Long AttendanceMeetings;
    private int contributionRate;
}
    
    