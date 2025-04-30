package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sunday.projectpop.model.entity.UserTrait;

import java.util.Set;

public interface UserTraitRepository extends JpaRepository<UserTrait, String> {

    UserTrait findByuserId(String id);

    @Query("""
    SELECT s.userId FROM UserTrait s
    WHERE s.userId IN :userIds
      AND s.openness BETWEEN :oMin AND :oMax
      AND s.conscientiousness BETWEEN :cMin AND :cMax
      AND s.extraversion BETWEEN :eMin AND :eMax
      AND s.agreeableness BETWEEN :aMin AND :aMax
      AND s.neuroticism BETWEEN :nMin AND :nMax
    """)
    Set<String> findUserIdsforMatchingLeader(
            @Param("userIds") Set<String> userIds,
            @Param("oMin") int oMin, @Param("oMax") int oMax,
            @Param("cMin") int cMin, @Param("cMax") int cMax,
            @Param("eMin") int eMin, @Param("eMax") int eMax,
            @Param("aMin") int aMin, @Param("aMax") int aMax,
            @Param("nMin") int nMin, @Param("nMax") int nMax
    );
}