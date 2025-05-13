package org.sunday.projectpop.model.dto;

import java.time.LocalDateTime;

public record MessageDto(
        Long id,
        String senderId,
        String receiverId,
        String content,
        boolean checking,
        LocalDateTime sentAt
) {}
