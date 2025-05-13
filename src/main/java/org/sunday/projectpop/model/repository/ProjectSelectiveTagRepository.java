package org.sunday.projectpop.project.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectSelectiveTagRepository extends JpaRepository<ProjectSelectiveTag, Long> {
    List<ProjectSelectiveTag> findByProject_ProjectId(String projectId);
}