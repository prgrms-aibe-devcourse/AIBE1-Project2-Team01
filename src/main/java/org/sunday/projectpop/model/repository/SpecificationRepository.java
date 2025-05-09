package org.sunday.projectpop.model.repository;

import org.sunday.projectpop.model.entity.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SpecificationRepository extends JpaRepository<Specification, String> {
    boolean existsByRequirement(String requirement);

    // 프로젝트 ID로 모든 명세서를 찾는 메서드
    List<Specification> findByOnGoingProject_Id(String projectId); // 수정됨

    // 특정 프로젝트의 상태가 '진행 완료'인 명세서의 수를 카운트
    int countByOnGoingProject_IdAndStatus(String onGoingProjectId, String status); // 수정됨

    // 프로젝트 ID로 모든 명세서의 수를 카운트
    int countByOnGoingProject_Id(String onGoingProjectId); // 수정됨



    // 예: 특정 상태의 명세서 찾기
    // List<Specification> findByStatus(String status);
}