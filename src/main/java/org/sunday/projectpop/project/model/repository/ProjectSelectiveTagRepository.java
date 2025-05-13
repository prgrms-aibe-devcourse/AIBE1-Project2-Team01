package org.sunday.projectpop.project.model.repository;

import org.sunday.projectpop.project.model.entity.ProjectSelectiveTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectSelectiveTagRepository extends JpaRepository<ProjectSelectiveTag, Long> {
    List<ProjectSelectiveTag> findByProject_ProjectId(String projectId);
}