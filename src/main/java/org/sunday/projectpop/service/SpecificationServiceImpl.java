package org.sunday.projectpop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.Specification;
import org.sunday.projectpop.model.repository.SpecificationRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SpecificationServiceImpl implements SpecificationService {

    private final SpecificationRepository specificationRepository;  // 자동 주입

    // 날짜 포맷터를 미리 정의하여 재사용성을 높임
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                .orElseThrow(() -> new RuntimeException("Specification을 찾을 수 없습니다."));
    }

    @Override
    public List<Specification> getSpecificationsByProjectId(Long onGoingProjectId) {
        // 프로젝트 ID를 기준으로 명세서를 가져오는 로직
        return specificationRepository.findByOnGoingProject_OnGoingProjectId(onGoingProjectId);  // 변경된 메소드 호출
    }

    @Override
    public long countCompletedSpecifications(Long projectId) {
        // 진행 완료된 명세서의 수를 카운트
        return specificationRepository.countByOnGoingProject_OnGoingProjectIdAndStatus(projectId, "완료");  // 변경된 메소드 호출, status값 변경
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
        // 해당 프로젝트의 명세서 목록을 가져온다.
        List<Specification> specifications = specificationRepository.findByOnGoingProject_OnGoingProjectId(onGoingProjectId);
        // 가져온 명세서 목록을 SpecificationDto로 변환하여 반환한다.
        return specifications.stream()
                .map(spec -> {
                    SpecificationDto dto = new SpecificationDto();
                    dto.setId(spec.getSpecificationId());
                    dto.setOnGoingProjectId(onGoingProjectId);
                    dto.setRequirement(spec.getRequirement());
                    dto.setAssignee(spec.getAssignee());
                    dto.setStatus(spec.getStatus());
                    // 날짜를 yyyy-MM-dd 형식의 문자열로 변환하여 설정한다.
                    dto.setDueDate(spec.getDueDate() != null ? dateFormatter.format(spec.getDueDate()) : null);
                    dto.setProgressRate(spec.getProgressRate());
                    // 날짜/시간을 yyyy-MM-dd HH:mm:ss 형식의 문자열로 변환하여 설정한다.
                    dto.setCreatedAt(spec.getCreatedAt() != null ? dateTimeFormatter.format(spec.getCreatedAt()) : null);
                    dto.setUpdatedAt(spec.getUpdatedAt() != null ? dateTimeFormatter.format(spec.getUpdatedAt()) : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
