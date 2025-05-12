package org.sunday.projectpop.service.matching;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.FitOcean;
import org.sunday.projectpop.model.dto.OCEAN;
import org.sunday.projectpop.model.entity.TraitMatch;
import org.sunday.projectpop.model.entity.UserTrait;
import org.sunday.projectpop.model.repository.ProjectRepository;
import org.sunday.projectpop.model.repository.TraitMatchRepository;
import org.sunday.projectpop.model.repository.UserTraitRepository;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TraitTableService {
    private final ProjectRepository projectRepository;
    private final TraitMatchRepository traitMatchRepository;
    private final UserTraitRepository userTraitRepository;


    // leader ocean 값 출력
    public OCEAN getLeaderOCEAN(String leaderId) {
        UserTrait trait = userTraitRepository.findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리더 없음: " + leaderId));
        return new OCEAN(
                trait.getOpenness(),
                trait.getConscientiousness(),
                trait.getExtraversion(),
                trait.getAgreeableness(),
                trait.getNeuroticism()
        );
    }

    // leader 오션 값 기반 적합한 값 출력
    public FitOcean fitOCEANFollower(OCEAN ocean) {
        String key = "" + ocean.o() + ocean.c() + ocean.e() + ocean.a() + ocean.n();
        TraitMatch traitMatch= traitMatchRepository.findById(key)
                .orElseThrow(() -> new NoSuchElementException("해당 key에 대한 TraitMatch 없음"));
        return new FitOcean(
                traitMatch.getOpenness(),
                traitMatch.getConscientiousness(),
                traitMatch.getExtraversion(),
                traitMatch.getAgreeableness(),
                traitMatch.getNeuroticism()
        );
    }
    // 적합한 follower set 출력
    public Set<String> getTraitsMatching(@Param("user_id") String leader,Set<String> userIds) {
        OCEAN ocean=getLeaderOCEAN(leader);
        FitOcean fitOcean=fitOCEANFollower(ocean);
        Set<String> answer=null;
        for (int i=1;i<=5;i++) {
            OCEAN minBound=fitOcean.minBoundary(1);
            OCEAN maxBound=fitOcean.maxBoundary(1);
            answer=userTraitRepository.findUserIdsforMatchingLeader(
                    userIds,
                    minBound.o(), maxBound.o(),
                    minBound.c(), maxBound.c(),
                    minBound.e(), maxBound.e(),
                    minBound.a(), maxBound.a(),
                    minBound.n(), maxBound.n() );
            if (answer !=null && ! answer.isEmpty()) {
                return answer;
            }

        }
        // 가능성은 없는 error
        throw new NoSuchElementException();
    }
}
