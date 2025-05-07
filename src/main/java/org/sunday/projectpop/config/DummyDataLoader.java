package org.sunday.projectpop.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.sunday.projectpop.project.model.entity.*;
import org.sunday.projectpop.project.model.repository.ProjectRepository;
import org.sunday.projectpop.project.model.repository.ProjectRequireTagRepository;
import org.sunday.projectpop.project.model.repository.ProjectSelectiveTagRepository;
import org.sunday.projectpop.project.model.repository.SkillTagRepository;
import org.sunday.projectpop.temp.user.UserAccountRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class DummyDataLoader implements CommandLineRunner {

    private final SkillTagRepository skillTagRepository;
    private final UserAccountRepository userAccountRepository;

    // ✅ 아래 추가
    private final ProjectRepository projectRepository;
    private final ProjectRequireTagRepository requireTagRepository;
    private final ProjectSelectiveTagRepository selectiveTagRepository;

    @Override
    public void run(String... args) throws Exception {
        // 유저
        if (userAccountRepository.count() == 0) {
            userAccountRepository.save(UserAccount.builder()
                    .userId("test-user-id")
                    .email("test@example.com")
                    .password("encoded-password")
                    .provider("LOCAL")
                    .admin(false)
                    .banned(false)
                    .build());
        }

        // 태그
        if (skillTagRepository.count() == 0) {
            skillTagRepository.save(SkillTag.builder().name("Java").build());    // id = 1
            skillTagRepository.save(SkillTag.builder().name("Spring").build());  // id = 2
            skillTagRepository.save(SkillTag.builder().name("React").build());   // id = 3
        }

        // ✅ 공고 더미
        if (projectRepository.count() == 0) {
            UserAccount leader = userAccountRepository.findById("test-user-id").orElseThrow();
            SkillTag java = skillTagRepository.findById(1L).orElseThrow();
            SkillTag spring = skillTagRepository.findById(2L).orElseThrow();

            Project project = Project.builder()
                    .title("AI 프로젝트 테스트")
                    .description("AI 기반 추천 시스템 개발")
                    .type("project")
                    .locationType("REMOTE")
                    .durationWeeks(4)
                    .teamSize(5)
                    .status("모집중")
                    .generatedByAi(false)
                    .createdAt(LocalDateTime.now())
                    .field("AI")
                    .leader(leader)
                    .build();

            Project saved = projectRepository.save(project);

            requireTagRepository.save(ProjectRequireTag.builder().project(saved).tag(java).build());
            selectiveTagRepository.save(ProjectSelectiveTag.builder().project(saved).tag(spring).build());
        }
    }
}
