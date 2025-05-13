package org.sunday.projectpop.service.matching;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.TraitMatch;
import org.sunday.projectpop.model.entity.UserTrait;
import org.sunday.projectpop.model.repository.TraitMatchRepository;
import org.sunday.projectpop.model.repository.UserTraitRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompatibilityService {

    private final UserTraitRepository userTraitRepository;
    private final TraitMatchRepository traitMatchRepository;

    public CompatibilityResult calculateCompatibility(String userId, String leaderOceanKey) {
        UserTrait user = userTraitRepository.findById(userId).orElseThrow();
        TraitMatch ideal = traitMatchRepository.findById(leaderOceanKey).orElseThrow();

        double distanceSquared =
                Math.pow(user.getOpenness() - ideal.getOpenness(), 2) +
                        Math.pow(user.getConscientiousness() - ideal.getConscientiousness(), 2) +
                        Math.pow(user.getExtraversion() - ideal.getExtraversion(), 2) +
                        Math.pow(user.getAgreeableness() - ideal.getAgreeableness(), 2) +
                        Math.pow(user.getNeuroticism() - ideal.getNeuroticism(), 2);

        double normalized = Math.exp(-distanceSquared / 10.0); // 자연스럽게 퍼짐
        int score = (int) Math.round(normalized * 40 + 60);    // 60~100 사이로 매핑

        return new CompatibilityResult(
                List.of(user.getOpenness(), user.getConscientiousness(), user.getExtraversion(),
                        user.getAgreeableness(), user.getNeuroticism()),
                score
        );
    }
    public int calculateCompatibility(UserTrait user, TraitMatch ideal) {
        double sumSq = Math.pow(user.getOpenness() - ideal.getOpenness(), 2)
                + Math.pow(user.getConscientiousness() - ideal.getConscientiousness(), 2)
                + Math.pow(user.getExtraversion() - ideal.getExtraversion(), 2)
                + Math.pow(user.getAgreeableness() - ideal.getAgreeableness(), 2)
                + Math.pow(user.getNeuroticism() - ideal.getNeuroticism(), 2);

        // 거리 기반 지수 감쇠 → exp(-d/10)
        double similarity = Math.exp(-sumSq / 10.0);

        // 60 ~ 100 점수 매핑
        int score = (int) Math.round(similarity * 40 + 60);
        return score;
    }

    public String makeLeaderOceanKey(UserTrait trait) {
        return String.valueOf(trait.getOpenness())
                + trait.getConscientiousness()
                + trait.getExtraversion()
                + trait.getAgreeableness()
                + trait.getNeuroticism();
    }

    public List<MatchedUserResult> getSortedUserCompatibility(String leaderUserId, List<String> userIds) {
        UserTrait leaderTrait = userTraitRepository.findById(leaderUserId).orElseThrow();
        String leaderKey = makeLeaderOceanKey(leaderTrait);
        TraitMatch ideal = traitMatchRepository.findById(leaderKey).orElseThrow();

        List<UserTrait> candidates = userTraitRepository.findAllById(userIds);

        return candidates.stream()
                .map(u -> new MatchedUserResult(u.getUser().getUserId(), calculateCompatibility(u, ideal)))
                .sorted(Comparator.comparingInt(MatchedUserResult::compatibilityScore).reversed())
                .toList();
    }


    public record MatchedUserResult(
            String userId,
            int compatibilityScore
    ) {}

    public record CompatibilityResult(List<Integer> scores, int compatibility) {}
}
