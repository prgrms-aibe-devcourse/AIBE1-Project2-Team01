package org.sunday.projectpop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.Specification;
import org.sunday.projectpop.model.repository.SpecificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecificationServiceImpl implements SpecificationService {

    private final SpecificationRepository specificationRepository;  // 자동 주입

    @Override
    public List<Specification> findAll() {
        return specificationRepository.findAll();
    }

    @Override
    public Specification save(Specification specification) {
        return specificationRepository.save(specification);
    }

    @Override
    public void delete(long id) {
        specificationRepository.deleteById(id);
    }

    @Override
    public Specification findById(long id) {
        return specificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specification not found"));
    }

    @Override
    public List<Specification> getSpecificationsByProjectId(Long onGoingProjectId) {
        // 프로젝트 ID를 기준으로 명세서를 가져오는 로직
        return specificationRepository.findByOnGoingProject_OnGoingProjectId(onGoingProjectId);  // 변경된 메소드 호출
    }

    @Override
    public long countCompletedSpecifications(Long projectId) {
        // 진행 완료된 명세서의 수를 카운트
        return specificationRepository.countByOnGoingProject_OnGoingProjectIdAndStatus(projectId, "SUCCESS");  // 변경된 메소드 호출
    }

    @Override
    public long countAllSpecifications(Long projectId) {
        // 모든 명세서의 수를 카운트
        return specificationRepository.countByOnGoingProject_OnGoingProjectId(projectId);  // 변경된 메소드 호출
    }

    @Override
    public int calculateProgressPercentage(Long projectId) {
        long completed = countCompletedSpecifications(projectId);
        long total = countAllSpecifications(projectId);

        if (total == 0) {
            return 0; // 0으로 나누지 않도록 주의
        }

        return (int) ((double) completed / total * 100);
    }

    @Override
    public List<SpecificationDto> getSpecificationsDtoByProjectId(Long onGoingProjectId) {
        return List.of();
    }

//    @Override
//    public List<SpecificationDto> getSpecificationsDtoByProjectId(Long onGoingProjectId) {
//        return List.of();
//    }
}
