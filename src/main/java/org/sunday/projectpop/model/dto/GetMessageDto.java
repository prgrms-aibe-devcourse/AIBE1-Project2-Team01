package org.sunday.projectpop.model.dto;

public record GetMessageDto(
        String senderId,
        String receiverId,
        String message
) {}
