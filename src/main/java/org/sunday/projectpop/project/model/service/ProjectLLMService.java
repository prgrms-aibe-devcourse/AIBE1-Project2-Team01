package org.sunday.projectpop.project.model.service;
//유저 기반 프롬프트 생성
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.project.model.entity.UserProfile;
import org.sunday.projectpop.project.model.entity.UserSkillTag;
import org.sunday.projectpop.project.model.repository.UserProfileRepository;
import org.sunday.projectpop.project.model.repository.UserSkillTagRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectLLMService {

    private final UserProfileRepository userProfileRepository;
    private final UserSkillTagRepository userSkillTagRepository;

    public String generatePrompt(String userId) {
        // 1. 프로필 가져오기
        UserProfile profile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 없습니다."));

        // 2. 스킬 태그 가져오기
        List<UserSkillTag> skillTags = userSkillTagRepository.findByUser_UserId(userId);
        List<String> tagNames = skillTags.stream()
                .map(tag -> tag.getTag().getName())
                .toList();

        // 3. LLM 프롬프트 구성
        String prompt = String.format("""
            당신은 사용자의 프로필 정보를 기반으로 프로젝트 공고를 자동 생성하는 AI입니다.

            [사용자 정보]
            닉네임: %s
            자기소개: %s
            보유 기술: %s

            [요청 사항]
            아래 항목을 JSON 형식으로 작성해주세요. 
            - title (공고 제목)
            - description (프로젝트 설명)
            - teamSize (모집 인원, 숫자)
            - requiredTags (필수 기술 태그, 문자열 배열)
            - selectiveTags (선택 기술 태그, 문자열 배열)
            - field (분야: 웹, 앱, AI, 게임 등 중 하나로 선택)
            - locationType (대면 또는 비대면 중 하나 선택)
            - durationWeeks (진행 기간, 숫자)

            [응답 예시]
            {
              "title": "Spring 기반 협업 프로젝트",
              "description": "함께 성장하는 백엔드 팀원을 찾습니다.",
              "teamSize": 4,
              "requiredTags": ["Java", "Spring Boot"],
              "selectiveTags": ["MySQL"],
              "field": "웹",
              "locationType": "비대면",
              "durationWeeks": 6
            }
            """, profile.getNickname(), profile.getBio(), String.join(", ", tagNames));

        return prompt;
    }
}