package org.sunday.projectpop.auth.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.Users;
import org.sunday.projectpop.model.repository.UsersRepository;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UsersRepository usersRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId(); // kakao, github, google
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId;
        String email = null;

        if (provider.equals("kakao")) {
            providerId = attributes.get("id").toString();
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null && kakaoAccount.get("email") != null) {
                email = kakaoAccount.get("email").toString();
            }

        } else if (provider.equals("github")) {
            providerId = attributes.get("id").toString();
            email = attributes.get("email") != null ? attributes.get("email").toString() : null;

        } else if (provider.equals("google")) {
            providerId = attributes.get("sub").toString(); // Google은 'sub'가 고유 ID
            email = attributes.get("email").toString();

        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + provider);
        }

        if (email == null || email.isBlank()) {
            email = provider + "_" + providerId + "@noemail.com";
        }

        final String safeEmail = email;

        // 유저가 없으면 새로 저장
        usersRepository.findByEmail(safeEmail)
                .orElseGet(() -> {
                    Users newUser = new Users();
                    newUser.setEmail(safeEmail);
                    newUser.setPassword(""); // 소셜 로그인은 비밀번호 없음
                    newUser.setRole("USER");
                    newUser.setProvider(provider);
                    newUser.setProviderId(providerId);
                    log.info("🔥 새 유저 저장됨: {}", newUser.getEmail());
                    return usersRepository.save(newUser);
                });

        // ✅ 영속 상태로 다시 불러오기
        Users user = usersRepository.findByEmail(safeEmail).orElseThrow();

        log.info("✅ 소셜 로그인 유저 이메일: {}", safeEmail);

        // 👉 기본 프로필 자동 생성 제거됨
        // userProfileService.createDefaultProfileIfNotExists(user);

        return new CustomOAuth2User(user.getEmail(), user.getRole());
    }
}
