package org.sunday.projectpop.service.specification;

import org.sunday.projectpop.model.dto.MemberContributionDto;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.Specification;
import java.util.List;
import java.util.Optional;

public interface SpecificationService {
    List<Specification> findAll();
    Specification save(Specification specification);
    void delete(String id);
    Optional<Specification> findById(String id); // Optional로 반환하도록 수정
    List<Specification> getSpecificationsByProjectId(String onGoingProjectId);

    // 진행률 계산을 위한 메소드 추가
    int countCompletedSpecifications(String projectId);
    int countAllSpecifications(String projectId);
    int calculateProgressPercentage(String projectId);

    List<SpecificationDto> getSpecificationsDtoByProjectId(String onGoingProjectId);
    SpecificationDto convertToDto(Specification specification); // DTO 변환 메서드 추가
    Specification convertToEntity(SpecificationDto dto);     // 엔티티 변환 메서드 추가

    List<MemberContributionDto> calculateMemberContributions(String onGoingProjectId);

    void updateSpecification(String id, SpecificationDto specificationDto); // 업데이트 메서드 추가
}