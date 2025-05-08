package org.sunday.projectpop.project.model.repository;

import org.sunday.projectpop.project.model.entity.ProjectRequireTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRequireTagRepository extends JpaRepository<ProjectRequireTag, Long> {
    List<ProjectRequireTag> findByProject_ProjectId(String projectId);
}