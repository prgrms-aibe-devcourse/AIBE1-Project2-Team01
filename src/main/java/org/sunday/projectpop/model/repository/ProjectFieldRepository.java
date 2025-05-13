package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.model.entity.ProjectField;

import java.util.Optional;

public interface ProjectFieldRepository extends JpaRepository<ProjectField, Long> {
    Optional<ProjectField> findByName(String name);

}
