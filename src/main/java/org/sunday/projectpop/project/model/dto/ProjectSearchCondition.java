package org.sunday.projectpop.project.model.dto;

import lombok.Getter;
import lombok.Setter;

// 검색 조건을 담는 DTO
@Getter
@Setter
public class ProjectSearchCondition {
    private String keyword;         // 검색어 (예: "AI")
    private String type;            // 프로젝트 타입 (예: "project" or "competition")
    private String field;           // 관심 분야
    private String locationType;    // 대면/비대면
    private String status;          // 모집 상태 (모집중/진행중/완료)
    private String sortBy;
    private Integer page = 0;  // 페이지 번호 (0부터 시작)
    private Integer size = 10; // 한 페이지당 항목 수
}

