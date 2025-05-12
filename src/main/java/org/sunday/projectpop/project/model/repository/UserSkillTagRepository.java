package org.sunday.projectpop.project.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.project.model.entity.UserSkillTag;

import java.util.List;


public interface UserSkillTagRepository extends JpaRepository<UserSkillTag, Long> {
    List<UserSkillTag> findByUser_UserId(String userId);
}