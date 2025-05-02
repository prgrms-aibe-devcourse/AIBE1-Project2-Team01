package org.sunday.projectpop.project.model.repository;

import org.sunday.projectpop.project.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
}
