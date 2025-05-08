package org.sunday.projectpop.service;

import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.MemberContributionDto;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.Specification;
import java.util.List;

//@Service
public interface SpecificationService {
    List<Specification> findAll();
    Specification save(Specification specification);
    void delete(long id);
    Specification findById(long id);
    List<Specification> getSpecificationsByProjectId(Long onGoingProjectId);

    // 진행률 계산을 위한 메소드 추가
    long countCompletedSpecifications(Long projectId);
    long countAllSpecifications(Long projectId);
    int calculateProgressPercentage(Long projectId);

    List<SpecificationDto> getSpecificationsDtoByProjectId(Long onGoingProjectId);

    List<MemberContributionDto> calculateMemberContributions(Long onGoingProjectId);
}
