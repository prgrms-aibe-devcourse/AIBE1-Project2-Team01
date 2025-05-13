package org.sunday.projectpop.service.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunday.projectpop.model.entity.Project;
import org.sunday.projectpop.model.entity.ProjectApplication;
import org.sunday.projectpop.model.entity.UserAccount;
import org.sunday.projectpop.model.repository.ProjectApplicationRepository;
import org.sunday.projectpop.model.repository.UserAccountRepository;
import org.sunday.projectpop.model.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectApplicationService {

    private final ProjectRepository projectRepository;
    private final UserAccountRepository userAccountRepository;
    private final ProjectApplicationRepository applicationRepository;

    @Transactional
    public void applyToProject(String projectId, String userId) {
        // 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트가 없습니다."));

        // 사용자 조회
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 지원했는지 확인
        if (applicationRepository.existsByProjectAndUser(project, user)) {
            throw new IllegalStateException("이미 이 프로젝트에 지원하셨습니다.");
        }

        // 지원 정보 저장
        ProjectApplication application = new ProjectApplication(project, user);
        applicationRepository.save(application);
    }
}