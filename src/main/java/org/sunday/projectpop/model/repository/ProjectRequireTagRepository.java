package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sunday.projectpop.model.entity.ProjectRequireTag;
import org.sunday.projectpop.model.entity.SkillTag;

import java.util.List;

public interface ProjectRequireTagRepository extends JpaRepository<ProjectRequireTag, Long> {
    //프로젝트 아이디 바탕으로 요구하는 기술 사항 추출
    @Query("""
    SELECT prt.tag.tagId
    FROM ProjectRequireTag prt
    WHERE prt.project.projectId = :projectId
    """)
    List<Long> findTagIdsByProjectId(@Param("projectId") String projectId);

    @Query("SELECT pr.tag FROM ProjectRequireTag pr WHERE pr.project.projectId = :projectId")
    List<SkillTag> findTagsByProjectId(@Param("projectId") String projectId);
    List<org.sunday.projectpop.model.entity.ProjectRequireTag> findByProject_ProjectId(String projectId);

}
