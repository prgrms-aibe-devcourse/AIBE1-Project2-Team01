package org.sunday.projectpop.service.specification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.MemberContributionDto;
import org.sunday.projectpop.model.dto.SpecificationDto;
import org.sunday.projectpop.model.entity.OnGoingProject;
import org.sunday.projectpop.model.entity.Specification;
import org.sunday.projectpop.model.repository.OnGoingProjectRepository;
import org.sunday.projectpop.model.repository.SpecificationRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecificationServiceImpl implements SpecificationService {

    private final SpecificationRepository specificationRepository;  // 자동 주입
    private final OnGoingProjectRepository onGoingProjectRepository; // OnGoingProjectRepository 주입

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
    public void delete(String id) {
        specificationRepository.deleteById(id);
    }

    @Override
    public Optional<Specification> findById(String id) {
        return specificationRepository.findById(id);
    }

    @Override
    public List<Specification> getSpecificationsByProjectId(String onGoingProjectId) {
        // 프로젝트 ID를 기준으로 명세서를 가져오는 로직
        return specificationRepository.findByOnGoingProject_Id(onGoingProjectId);  // 변경된 메소드 호출
    }

    @Override
    public int countCompletedSpecifications(String projectId) {
        // 진행 완료된 명세서의 수를 카운트
        return specificationRepository.countByOnGoingProject_IdAndStatus(projectId, "completed");  // 변경된 메소드 호출, status값 변경
    }

    @Override
    public int countAllSpecifications(String projectId) {
        // 모든 명세서의 수를 카운트
        return specificationRepository.countByOnGoingProject_Id(projectId);  // 변경된 메소드 호출
    }

    @Override
    public int calculateProgressPercentage(String projectId) {
        int completed = countCompletedSpecifications(projectId);
        int total = countAllSpecifications(projectId);

        if (total == 0) {
            return 0; // 0으로 나누지 않도록 주의
        }

        return (int) ((double) completed / total * 100);
    }

    @Override
    public List<SpecificationDto> getSpecificationsDtoByProjectId(String onGoingProjectId) {
        // 해당 프로젝트의 명세서 목록을 가져온다.
        List<Specification> specifications = specificationRepository.findByOnGoingProject_Id(onGoingProjectId);
        // 가져온 명세서 목록을 SpecificationDto로 변환하여 반환한다.
        return specifications.stream()
                .map(this::convertToDto) // convertToDto 메서드 사용
                .collect(Collectors.toList());
    }

    @Override
    public SpecificationDto convertToDto(Specification specification) {
        SpecificationDto dto = new SpecificationDto();
        dto.setId(specification.getId());
        dto.setOnGoingProjectId(specification.getOnGoingProject().getId());
        dto.setRequirement(specification.getRequirement());
        dto.setAssignee(specification.getAssignee());
        dto.setStatus(specification.getStatus());
        // 날짜를 yyyy-MM-dd 형식의 문자열로 변환하여 설정한다.
        dto.setDueDate(specification.getDueDate() != null ? dateFormatter.format(specification.getDueDate()) : null);
        // 날짜/시간을 yyyy-MM-dd HH:mm:ss 형식의 문자열로 변환하여 설정한다.
        dto.setCreatedAt(specification.getCreatedAt() != null ? dateTimeFormatter.format(specification.getCreatedAt()) : null);
        dto.setUpdatedAt(specification.getUpdatedAt() != null ? dateTimeFormatter.format(specification.getUpdatedAt()) : null);
        return dto;
    }

    @Override
    public Specification convertToEntity(SpecificationDto dto) {
        Specification specification = new Specification();
        Optional<OnGoingProject> projectOpt = onGoingProjectRepository.findById(dto.getOnGoingProjectId());
        projectOpt.ifPresent(specification::setOnGoingProject);
        specification.setRequirement(dto.getRequirement());
        specification.setAssignee(dto.getAssignee());
        specification.setStatus(dto.getStatus());
        // 문자열 형식의 날짜를 LocalDate로 변환하여 설정
        if (dto.getDueDate() != null && !dto.getDueDate().isEmpty()) {
            specification.setDueDate(java.time.LocalDate.parse(dto.getDueDate(), dateFormatter));
        }
        return specification;
    }

    @Override
    // 새로운 메서드: 팀원별 기여도 계산
    public List<MemberContributionDto> calculateMemberContributions(String onGoingProjectId) {
        List<Specification> specifications = specificationRepository.findByOnGoingProject_Id(onGoingProjectId);
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

    @Transactional
    @Override
    public void updateSpecification(String id, SpecificationDto specificationDto) {
        Optional<Specification> specificationOpt = specificationRepository.findById(id);
        if (specificationOpt.isPresent()) {
            Specification specification = specificationOpt.get();
            Optional<OnGoingProject> projectOpt = onGoingProjectRepository.findById(specificationDto.getOnGoingProjectId());
            projectOpt.ifPresent(specification::setOnGoingProject);
            specification.setRequirement(specificationDto.getRequirement());
            specification.setAssignee(specificationDto.getAssignee());
            specification.setStatus(specificationDto.getStatus());
            // 문자열 형식의 날짜를 LocalDate로 변환하여 설정
            if (specificationDto.getDueDate() != null && !specificationDto.getDueDate().isEmpty()) {
                specification.setDueDate(java.time.LocalDate.parse(specificationDto.getDueDate(), dateFormatter));
            }
            specificationRepository.save(specification);
        } else {
            throw new RuntimeException("수정할 명세서를 찾을 수 없습니다.");
        }
    }
}