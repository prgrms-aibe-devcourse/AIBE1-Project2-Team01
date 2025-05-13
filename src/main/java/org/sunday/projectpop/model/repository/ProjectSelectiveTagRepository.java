package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.model.entity.ProjectSelectiveTag;

import java.util.List;

public interface ProjectSelectiveTagRepository extends JpaRepository<ProjectSelectiveTag, Long> {
    List<ProjectSelectiveTag> findByProject_ProjectId(String projectId);
}