package org.sunday.projectpop.service.ongoingproject;

import org.sunday.projectpop.model.entity.OnGoingProject;

import java.util.List;
import java.util.Optional;

public interface OnGoingProjectService {
    Optional<OnGoingProject> findById(String id);  // 프로젝트 아이디로 조회

    boolean existsById(String id);  // 프로젝트 아이디로 존재 여부 체크

    List<OnGoingProject> findAll();  // 모든 프로젝트 리스트 조회
}
