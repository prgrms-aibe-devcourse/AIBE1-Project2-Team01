package org.sunday.projectpop.dto.auth;

public record JoinDTO(
        String email,
        String password,
        String nickname // 별도로 프로필에 저장해도 되지만 회원가입 시 필요
) {}
