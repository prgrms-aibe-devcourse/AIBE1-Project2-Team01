package org.sunday.projectpop.service;

import org.sunday.projectpop.model.dto.SpecificationDto;

import java.util.List;

public interface HealthCheckService {
    int calculateScheduleCompliance(List<SpecificationDto> specifications);
    int calculateRiskStatus(List<SpecificationDto> specifications);
}
