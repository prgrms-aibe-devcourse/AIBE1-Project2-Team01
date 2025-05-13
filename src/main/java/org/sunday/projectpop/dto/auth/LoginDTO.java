package org.sunday.projectpop.dto.auth;

public record LoginDTO(
        String email,
        String password
) {}