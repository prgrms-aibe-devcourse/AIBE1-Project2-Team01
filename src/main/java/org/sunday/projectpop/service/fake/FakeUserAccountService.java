package org.sunday.projectpop.service.fake;


import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.UserAccount;
import org.sunday.projectpop.service.project.UserAccountService;
import org.sunday.projectpop.model.repository.UserAccountRepository;



import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FakeUserAccountService implements UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserAccount getUserById(String userId) {
        // DB에 없으면 새로 저장하고 반환
        return userAccountRepository.findById(userId)
                .orElseGet(() -> userAccountRepository.save(
                        UserAccount.builder()
                                .userId(userId)
                                .email("test@example.com")
                                .password("dummy")
                                .provider("local")
                                .admin(false)
                                .banned(false)
                                .build()
                ));
    }
}

