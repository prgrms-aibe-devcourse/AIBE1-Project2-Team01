package org.sunday.projectpop.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PortfolioNoteRequest(
        @NotEmpty(message = "내용은 필수 입력입니다.")
        @Size(max = 2000, message = "내용은 2000자 이하로 입력해주세요.")
        String content
) {
}
