package org.sunday.projectpop.auth.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

@Slf4j
public class LoggingTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> delegate;

    public LoggingTokenResponseClient(OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> delegate) {
        this.delegate = delegate;
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest request) {
        try {
            log.info("🔑 [OAuth2] 토큰 요청: {}", request.getClientRegistration().getRegistrationId());
            var response = delegate.getTokenResponse(request);
            log.info("✅ [OAuth2] 토큰 응답 성공: {}", response.getAccessToken().getTokenValue());
            return response;
        } catch (Exception e) {
            log.error("❌ [OAuth2] 토큰 응답 실패", e);
            throw e;
        }
    }
}