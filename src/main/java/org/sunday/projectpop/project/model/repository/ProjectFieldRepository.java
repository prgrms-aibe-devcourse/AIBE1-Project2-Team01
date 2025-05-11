package org.sunday.projectpop.project.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.project.model.entity.ProjectField;

public interface ProjectFieldRepository extends JpaRepository<ProjectField, Long> {
}
