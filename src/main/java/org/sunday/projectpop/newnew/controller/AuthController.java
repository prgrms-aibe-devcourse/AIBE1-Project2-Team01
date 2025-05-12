package org.sunday.projectpop.newnew.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.sunday.projectpop.newnew.auth.JwtTokenProvider;
import org.sunday.projectpop.newnew.dto.JoinDTO;
import org.sunday.projectpop.newnew.dto.LoginDTO;
import org.sunday.projectpop.newnew.service.UserService;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public void login(@RequestBody LoginDTO dto, HttpServletResponse response) throws IOException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
            );

            // 토큰 발급
            String token = jwtTokenProvider.generateToken(authentication, List.of("USER"));
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) Duration.ofHours(1).getSeconds());
            response.addCookie(cookie);

            // ✅ 로그인 성공 후 자동 리다이렉트
            response.sendRedirect("/profile/new"); // ← 여기 추가

        } catch (AuthenticationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 실패");
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody JoinDTO dto) {
        try {
            userService.signup(dto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // 쿠키 삭제
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료

        response.addCookie(cookie);

        // 시큐리티 컨텍스트 초기화 (optional)
        SecurityContextHolder.clearContext();

        return ResponseEntity.noContent().build();
    }

}