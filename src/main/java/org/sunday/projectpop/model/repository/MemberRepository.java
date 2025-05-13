package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.model.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
