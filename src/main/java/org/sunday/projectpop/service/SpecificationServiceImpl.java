package org.sunday.projectpop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.MemberContributionDto;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.Specification;
import org.sunday.projectpop.model.repository.SpecificationRepository;

import java.util.*;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

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
        return specificationRepository.countByOnGoingProject_OnGoingProjectIdAndStatus(projectId, "completed");  // 변경된 메소드 호출, status값 변경
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

    @Override
    // 새로운 메서드: 팀원별 기여도 계산
    public List<MemberContributionDto> calculateMemberContributions(Long onGoingProjectId) {
        List<Specification> specifications = specificationRepository.findByOnGoingProject_OnGoingProjectId(onGoingProjectId);
        // 1. 팀원별 완료한 작업 수 계산
        Map<String, Long> completedTaskCounts = specifications.stream()
                .filter(spec -> "completed".equals(spec.getStatus()))
                .collect(Collectors.groupingBy(Specification::getAssignee, Collectors.counting()));

        // 2. 총 작업 수 계산
        long totalTasks = specifications.size();

        // 3. 팀원별 기여도 계산 및 DTO 생성
        List<MemberContributionDto> contributions = new ArrayList<>();
        completedTaskCounts.forEach((assignee, completedCount) -> {
            double contributionRate = (totalTasks > 0) ? (double) completedCount / totalTasks * 100 : 0;
            contributions.add(new MemberContributionDto(assignee, completedCount, 0L, (int) Math.round(contributionRate)));
        });

        //만약 assignee가 null이거나 ""인 spec이 있다면, "Unassigned"로 처리해준다.
        if (specifications.stream().anyMatch(spec -> spec.getAssignee() == null || spec.getAssignee().isEmpty())) {
            long unassignedCompletedCount = specifications.stream()
                    .filter(spec -> (spec.getAssignee() == null || spec.getAssignee().isEmpty()) && "completed".equals(spec.getStatus()))
                    .count();
            double unassignedContributionRate = (totalTasks > 0) ? (double) unassignedCompletedCount / totalTasks * 100 : 0;
            contributions.add(new MemberContributionDto("Unassigned", unassignedCompletedCount, 0L, (int)Math.round(unassignedContributionRate)));
        }
        return contributions;
    }

}
