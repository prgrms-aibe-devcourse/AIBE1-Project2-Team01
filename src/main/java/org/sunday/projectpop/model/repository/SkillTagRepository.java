package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.model.entity.SkillTag;

import java.util.List;

public interface SkillTagRepository extends JpaRepository<SkillTag, Long> {
    List<SkillTag> findByNameIn(List<String> names);
}
