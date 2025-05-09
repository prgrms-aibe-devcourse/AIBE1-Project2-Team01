package org.sunday.projectpop.service;

import org.sunday.projectpop.model.dto.MemberContributionDto;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.Specification;
import java.util.List;
import java.util.Optional;

public interface SpecificationService {
    List<Specification> findAll();
    Specification save(Specification specification);
    void delete(long id);
    Optional<Specification> findById(long id); // Optional로 반환하도록 수정
    List<Specification> getSpecificationsByProjectId(Long onGoingProjectId);

    // 진행률 계산을 위한 메소드 추가
    long countCompletedSpecifications(Long projectId);
    long countAllSpecifications(Long projectId);
    int calculateProgressPercentage(Long projectId);

    List<SpecificationDto> getSpecificationsDtoByProjectId(Long onGoingProjectId);
    SpecificationDto convertToDto(Specification specification); // DTO 변환 메서드 추가
    Specification convertToEntity(SpecificationDto dto);     // 엔티티 변환 메서드 추가

    List<MemberContributionDto> calculateMemberContributions(Long onGoingProjectId);

    void updateSpecification(Long id, SpecificationDto specificationDto); // 업데이트 메서드 추가
}