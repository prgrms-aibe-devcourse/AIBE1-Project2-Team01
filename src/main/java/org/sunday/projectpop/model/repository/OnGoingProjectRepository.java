package org.sunday.projectpop.model.repository;

import org.springframework.stereotype.Repository;
import org.sunday.projectpop.model.entity.OnGoingProject;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OnGoingProjectRepository extends JpaRepository<OnGoingProject, String> {
}