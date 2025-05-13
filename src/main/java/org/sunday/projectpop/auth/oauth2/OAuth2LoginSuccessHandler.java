package org.sunday.projectpop.auth.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.sunday.projectpop.auth.jwt.JwtTokenProvider;
import org.sunday.projectpop.model.repository.UsersRepository;
import org.sunday.projectpop.model.repository.UserProfileRepository;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UsersRepository usersRepository;
    private final UserProfileRepository userProfileRepository;

    @Value("${front-end.redirect:/profile/view}")
    private String frontRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();
        log.info("✅ OAuth2 로그인 성공: {}", email);

        // ✅ JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(authentication, List.of("USER"));
        log.info("✅ JWT 토큰 발급 완료: {}", token);

        // ✅ HttpOnly 쿠키로 토큰 설정
        Cookie cookie = new Cookie("token", URLEncoder.encode(token, StandardCharsets.UTF_8));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1시간
        response.addCookie(cookie);
        log.info("🔁 쿠키에 토큰 설정 완료");

        // ✅ 사용자 프로필 존재 여부 확인
        var user = usersRepository.findByEmail(email).orElse(null);
        boolean hasProfile = user != null && userProfileRepository.existsById(user.getId());

        // ✅ 프로필 존재 여부에 따라 리디렉션 분기
        String redirectPath = hasProfile ? "/profile/view" : "/profile/new";
        log.info("🚀 리디렉션 경로 결정됨: {}", redirectPath);

        response.sendRedirect(redirectPath);
    }
}
