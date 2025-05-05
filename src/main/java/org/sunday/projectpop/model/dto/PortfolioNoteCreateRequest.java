package org.sunday.projectpop.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PortfolioNoteCreateRequest(
        @NotEmpty(message = "내용은 필수 입력입니다.")
        @Size(max = 5000, message = "내용은 5000자 이하로 입력해주세요.")
        String content
) {
}
