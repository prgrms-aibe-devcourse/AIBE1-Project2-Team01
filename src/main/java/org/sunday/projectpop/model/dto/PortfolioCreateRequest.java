package org.sunday.projectpop.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sunday.projectpop.model.enums.PortfoliosType;

import java.util.List;

public record PortfolioCreateRequest(
        @NotNull(message = "유형을 선택해주세요.")
        PortfoliosType portfolioType,

        @NotEmpty(message = "제목을 입력해주세요.")
        String title,

        @NotEmpty(message = "설명을 입력해주세요.")
        @Size(max = 2000, message = "설명은 2000자 이하로 입력해주세요.")
        String description,

        List<String> urls
) {
    public static PortfolioCreateRequest emptyCreateRequest() {
        return new PortfolioCreateRequest(null, null, null, null);
    }
}
