package org.sunday.projectpop.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 검색 조건을 담는 DTO
@Getter
@Setter
public class ProjectSearchCondition {
    private String keyword;         // 검색어 (예: "AI")
    private Long field;           // 관심 분야
    private List<String> skillTag;        // 기술 스택
    private List<String> experienceLevel; // 경력 수준
    private List<String> type;            // 종류 (project, competition)
    private List<String> locationType;  // 대면/비대면
    private List<String> status;         // 모집 상태 (모집중/진행중/완료)
    private String sortBy;
    private Integer page = 0;  // 페이지 번호 (0부터 시작)
    private Integer size = 10; // 한 페이지당 항목 수
}

