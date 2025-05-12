package org.sunday.projectpop.project.model.repository;

import org.sunday.projectpop.project.model.entity.SkillTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//공고 생성을 위해 임시 구현
public interface SkillTagRepository extends JpaRepository<SkillTag, Long> {
    List<SkillTag> findByNameIn(List<String> names);
}
