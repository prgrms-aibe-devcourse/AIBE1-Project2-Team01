package org.sunday.projectpop.temp.user;


import org.springframework.stereotype.Service;
import org.sunday.projectpop.project.model.entity.UserAccount;
import org.sunday.projectpop.project.model.service.UserAccountService;



import lombok.RequiredArgsConstructor;
import org.sunday.projectpop.project.model.entity.UserAccount;
import org.sunday.projectpop.temp.user.UserAccountRepository;
import org.sunday.projectpop.project.model.service.UserAccountService;
import org.springframework.stereotype.Service;

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

