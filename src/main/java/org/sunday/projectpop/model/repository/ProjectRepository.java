package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.sunday.projectpop.model.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // id 기반 project size 추출
    Integer findTeamSizeByProjectId(@Param("projectId") String projectId);

}
