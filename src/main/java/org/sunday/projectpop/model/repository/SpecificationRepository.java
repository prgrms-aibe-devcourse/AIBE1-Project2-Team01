package org.sunday.projectpop.model.repository;

import org.sunday.projectpop.model.entity.Specification; // 엔티티 Specification을 사용
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SpecificationRepository extends JpaRepository<Specification, Long> {

    // 프로젝트 ID로 모든 명세서를 찾는 메서드
    List<Specification> findByOnGoingProjectId(Long projectId);

    int countByonGoingProjectIdAndStatus(Long onGoingProjectId, String status);

    // 필요에 따라 다른 검색 메서드를 추가할 수 있습니다.
    // 예: 특정 담당자의 명세서 찾기
    // List<Specification> findByAssignee(String assignee);

    // 예: 특정 상태의 명세서 찾기
    // List<Specification> findByStatus(String status);
}
