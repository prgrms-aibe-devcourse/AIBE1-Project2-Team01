package org.sunday.projectpop.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// 📌 저장/수정용 (userId 없음)
public record UserProfileRequestDTO(
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2~20자 사이여야 합니다.")
        String nickname,

        @Size(max = 1000, message = "소개는 1000자 이내여야 합니다.")
        String bio,

        String profileImageUrl,

        @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phone
) {}