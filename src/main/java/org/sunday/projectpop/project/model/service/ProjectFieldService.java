package org.sunday.projectpop.project.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.project.model.entity.ProjectField;
import org.sunday.projectpop.project.model.repository.ProjectFieldRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectFieldService {

    private final ProjectFieldRepository projectFieldRepository;

    public List<ProjectField> getAllFields() {
        List<ProjectField> fields = projectFieldRepository.findAll();


        System.out.println("✅ 전체 분야 목록: " + fields.size());
        fields.forEach(field -> {
            System.out.println("🟣 " + field.getId() + " - " + field.getName());
        });

        return fields;
    }
}