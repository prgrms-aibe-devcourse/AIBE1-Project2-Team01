package org.sunday.projectpop.auth.oauth2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final String email;
    private final String role;

    public CustomOAuth2User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("email", email, "role", role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getName() {
        return email;
    }
}