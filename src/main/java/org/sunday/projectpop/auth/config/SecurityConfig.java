package org.sunday.projectpop.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.sunday.projectpop.auth.oauth2.CustomOAuth2UserService;
import org.sunday.projectpop.auth.jwt.JwtAuthenticationFilter;
import org.sunday.projectpop.auth.jwt.JwtTokenProvider;
import org.sunday.projectpop.auth.oauth2.OAuth2LoginSuccessHandler;
import org.sunday.projectpop.auth.client.LoggingTokenResponseClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        log.info("🔧 SecurityFilterChain 설정 시작됨");
        log.info("🔧 JwtAuthenticationFilter가 SecurityFilterChain에 추가됨");

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/signin", // ✅ 로그인 페이지만 예외
                                "/auth/signup",
                                "/css/**", "/js/**", "/images/**", "/favicon.ico"
                        ).permitAll()
//                        .anyRequest().authenticated() // 🔐 로그인 페이지 외 다른 페이지 잠금
                          .anyRequest().permitAll() // 🗝️ 잠금 해제
                )
                .formLogin(form -> form
                        .loginPage("/auth/signin")
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/auth/signin")
                        .tokenEndpoint(token -> token
                                .accessTokenResponseClient(accessTokenResponseClient())
                        )
                        .userInfoEndpoint(user -> user.userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("✅ SecurityFilterChain 빌드 완료");

        return http.build();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient delegate = new DefaultAuthorizationCodeTokenResponseClient();

        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(),
                new OAuth2AccessTokenResponseHttpMessageConverter()
        ));

        restTemplate.setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().set("Accept", "application/json");
            return execution.execute(request, body);
        }));

        delegate.setRestOperations(restTemplate);
        return new LoggingTokenResponseClient(delegate);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        log.info("🛡️ JwtAuthenticationFilter Bean 등록됨");
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authProvider()));
    }
}
