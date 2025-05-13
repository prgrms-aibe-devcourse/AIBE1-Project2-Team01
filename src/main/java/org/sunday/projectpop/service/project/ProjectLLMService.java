package org.sunday.projectpop.service.project;
//유저 기반 프롬프트 생성
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.ProjectField;
import org.sunday.projectpop.model.repository.*;
import org.sunday.projectpop.model.dto.GeminiResponse;
import org.sunday.projectpop.model.entity.*;
import org.sunday.projectpop.model.repository.UserProfileRepository;
import org.sunday.projectpop.model.repository.UserAccountRepository;
import org.sunday.projectpop.model.repository.ProjectRepository;



import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectLLMService {

    private final UserProfileRepository userProfileRepository;
    private final UserSkillTagRepository userSkillTagRepository;

    private final UserAccountRepository userAccountRepository;
    private final ProjectRepository projectRepository;
    private final ProjectFieldRepository projectFieldRepository;
    private final SkillTagRepository skillTagRepository;
    private final ProjectRequireTagRepository requireTagRepository;
    private final ProjectSelectiveTagRepository selectiveTagRepository;

    public String generatePrompt(String userId) {
        // 1. 프로필 가져오기
        UserProfile profile = userProfileRepository.findByUser_Id(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("프로필이 없습니다."));

        List<UserSkillTag> skillTags = userSkillTagRepository.findByUser_UserId(userId);
        List<String> tagNamesFromUser = skillTags.stream()
                .map(tag -> tag.getTag().getName())
                .toList();

        List<String> tagNamesFromDb = skillTagRepository.findAll().stream()
                .map(SkillTag::getName)
                .toList();

        List<String> fieldNames = projectFieldRepository.findAll().stream()
                .map(ProjectField::getName)
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
    단, field는 아래 리스트 중 하나에서 선택하고,
    requiredTags와 selectiveTags는 아래 기술 태그 목록 중에서만 선택하세요.

    - field 후보: %s
    - 기술 태그 목록: %s

    작성해야 할 항목
    - title (공고 제목)
    - description (프로젝트 설명)
    - teamSize (모집 인원, 숫자)
    - requiredTags (필수 기술 태그, 문자열 배열)
    - selectiveTags (선택 기술 태그, 문자열 배열)
    - field (분야)
    - durationWeeks (진행 기간, 숫자)

    [응답 예시]
    {
      "title": "Spring 기반 협업 프로젝트",
      "description": "함께 성장하는 백엔드 팀원을 찾습니다.",
      "teamSize": 4,
      "requiredTags": ["Java", "Spring Boot"],
      "selectiveTags": ["MySQL"],
      "field": "웹",
      "durationWeeks": 6
    }
    """, profile.getNickname(), profile.getBio(), String.join(", ", tagNamesFromUser),
                String.join(", ", fieldNames), String.join(", ", tagNamesFromDb));

        return prompt;
    }
    public void saveGeneratedProject(GeminiResponse response, String userId) {

        // 1. 유저 엔티티 찾기
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 필드 매핑
        ProjectField field = projectFieldRepository.findByName(response.field())
                .orElseThrow(() -> new IllegalArgumentException("해당 분야가 존재하지 않습니다."));

        // 3. 필수/선택 태그 매핑
        List<SkillTag> requiredTags = skillTagRepository.findByNameIn(response.requiredTags());
        List<SkillTag> selectiveTags = skillTagRepository.findByNameIn(response.selectiveTags());

        // 4. 프로젝트 저장
        Project project = Project.builder()
                .title(response.title())
                .description(response.description())
                .field(field)
                .teamSize(response.teamSize())
                .durationWeeks(response.durationWeeks())
                .type("PROJECT")
                .leader(user)
                .generatedByAi(true)
                .build();
        projectRepository.save(project);

        // 5. 태그 저장
        for (SkillTag tag : requiredTags) {
            ProjectRequireTag requireTag = new ProjectRequireTag(project, tag);
            requireTagRepository.save(requireTag);
        }

        for (SkillTag tag : selectiveTags) {
            ProjectSelectiveTag selectiveTag = new ProjectSelectiveTag(project, tag);
            selectiveTagRepository.save(selectiveTag);
        }
    }

}