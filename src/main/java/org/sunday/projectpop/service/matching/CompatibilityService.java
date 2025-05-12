package org.sunday.projectpop.service.matching;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.TraitMatch;
import org.sunday.projectpop.model.entity.UserTrait;
import org.sunday.projectpop.model.repository.TraitMatchRepository;
import org.sunday.projectpop.model.repository.UserTraitRepository;

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

    public record CompatibilityResult(List<Integer> scores, int compatibility) {}
}
