package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sunday.projectpop.model.entity.SkillTag;
import org.sunday.projectpop.model.entity.UserSkillTag;

import java.util.List;

public interface UserSkillTagRepository extends JpaRepository<UserSkillTag, Integer> {
    @Query("""
    SELECT ust.tag.tagId
    FROM UserSkillTag ust
    WHERE ust.user.userId = :userId
    """)
    List<Long> findTagIdsByUserId(@Param("userId")String userId);
//    List<SkillTag> findByNameIn(List<String> names); 필요시 생성
    @Query("SELECT s.name FROM SkillTag s WHERE s.tagId = :tagId")
    String findNameByTagId(@Param("tagId") Long tagId);

    @Query("SELECT ust.tag.name FROM UserSkillTag ust WHERE ust.user.userId = :userId")
    List<String> findTagNamesByUserId(@Param("userId") String userId);

    List<UserSkillTag> findByUser_UserId(String userId);
}
