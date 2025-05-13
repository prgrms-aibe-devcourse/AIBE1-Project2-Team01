package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.model.entity.Project;
import org.sunday.projectpop.model.entity.ProjectApplication;
import org.sunday.projectpop.model.entity.UserAccount;

public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {
    boolean existsByProjectAndUser(Project project, UserAccount user);
}
