package org.sunday.projectpop.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sunday.projectpop.model.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, String> {
    // id 기반 project size 추출
    @Query("SELECT p.teamSize FROM Project p WHERE p.projectId = :projectId")
    Integer findTeamSizeByProjectId(@Param("projectId") String projectId);
    // 프로젝트 리더 출력
    @Query("SELECT p.leader.userId FROM Project p WHERE p.projectId = :projectId")
    String findLeaderByProjectId(@Param("projectId") String projectId);

    Page<Project> findAll(Specification<Project> search, Pageable pageable);
}
