package org.sunday.projectpop.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sunday.projectpop.model.enums.PortfoliosType;

import java.util.List;

public record PortfolioCreateRequest(
        @NotNull(message = "유형은 필수 입력입니다.")
        PortfoliosType portfolioType,

        @NotEmpty(message = "URL은 필수 입력입니다.")
        String title,

        @NotEmpty(message = "설명은 필수 입력입니다.")
        @Size(max = 2000, message = "설명은 2000자 이하로 입력해주세요.")
        String description,

        List<String> urls

) {
}
