package org.sunday.projectpop.project.model.repository;

import org.sunday.projectpop.project.model.entity.ProjectRequireTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

//공고 생성을 위해 임시 구현
public interface ProjectRequireTagRepository extends JpaRepository<ProjectRequireTag, Long> {
    List<ProjectRequireTag> findByProject_ProjectId(String projectId);
}