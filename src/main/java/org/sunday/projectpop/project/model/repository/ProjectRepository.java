package org.sunday.projectpop.project.model.repository;

import org.sunday.projectpop.project.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

//  Specification 기능을 쓰려면 이 인터페이스도 같이 상속해야 함
public interface ProjectRepository extends JpaRepository<Project, String>, JpaSpecificationExecutor<Project> {
}
