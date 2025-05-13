package org.sunday.projectpop.controller.auth;

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
import org.sunday.projectpop.auth.jwt.JwtTokenProvider;
import org.sunday.projectpop.dto.auth.JoinDTO;
import org.sunday.projectpop.dto.auth.LoginDTO;
import org.sunday.projectpop.service.account.UserService;

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
    public ResponseEntity<?> login(@RequestBody LoginDTO dto, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
            );

            String token = jwtTokenProvider.generateToken(authentication, List.of("USER"));
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) Duration.ofHours(1).getSeconds());
            response.addCookie(cookie);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("로그인 실패. 이메일 또는 비밀번호를 확인해주세요.");
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