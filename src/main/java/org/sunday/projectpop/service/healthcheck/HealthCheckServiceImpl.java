package org.sunday.projectpop.service.healthcheck;

import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.SpecificationDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    @Override
    public int calculateScheduleCompliance(List<SpecificationDto> specifications) {
        int totalSpecs = specifications.size();
        int compliantCount = 0;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        for (SpecificationDto spec : specifications) {
            try {
                LocalDate dueDate = LocalDate.parse(spec.getDueDate(), formatter);
                String status = spec.getStatus();

                // 마감일 이전 또는 당일에 완료된 경우만 일정 준수
                if ("COMPLETED".equalsIgnoreCase(status) && !dueDate.isBefore(today)) {
                    compliantCount++;
                }
            } catch (Exception e) {
                System.err.println("날짜 파싱 실패: " + spec.getDueDate());
            }
        }

        return totalSpecs == 0 ? 0 : (compliantCount * 100) / totalSpecs;
    }


    @Override
    public int calculateRiskStatus(List<SpecificationDto> specifications) {
        int riskCount = 0;

        for (SpecificationDto spec : specifications) {
            if ("onGoing".equals(spec.getStatus())) {
                riskCount++; // onGoing 상태의 명세서를 리스크로 간주
            }
        }

        return riskCount;
    }

}
