package org.sunday.projectpop.model.dto;

public record MessageDto(
        String senderId,
        String receiverId,
        String message
) {}
