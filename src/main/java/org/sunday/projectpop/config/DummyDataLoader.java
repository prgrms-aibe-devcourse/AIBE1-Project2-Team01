package org.sunday.projectpop.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.sunday.projectpop.project.model.entity.SkillTag;
import org.sunday.projectpop.project.model.repository.SkillTagRepository;
import org.sunday.projectpop.project.model.entity.UserAccount;
import org.sunday.projectpop.temp.user.UserAccountRepository;

@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final SkillTagRepository skillTagRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public void run(String... args) throws Exception {
        // 유저 더미 데이터 삽입
        if (userAccountRepository.count() == 0) {
            userAccountRepository.save(UserAccount.builder()
                    .userId("test-user-id")
                    .email("test@example.com")
                    .password("encoded-password") // 실제 환경에선 인코딩 필수!
                    .provider("LOCAL")
                    .admin(false)
                    .banned(false)
                    .build());
        }

        // 스킬 태그 더미 데이터 삽입 (ID는 자동 생성)
        if (skillTagRepository.count() == 0) {
            skillTagRepository.save(SkillTag.builder().name("Java").build());
            skillTagRepository.save(SkillTag.builder().name("Spring").build());
            skillTagRepository.save(SkillTag.builder().name("React").build());
        }
    }
}
