package org.sunday.projectpop.service.matching;


import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.repository.ProjectRepository;
import org.sunday.projectpop.model.repository.ProjectRequireTagRepository;
import org.sunday.projectpop.model.repository.UserSkillTagRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class matchingService {
    private final RedisTagService redisTagService;
    private final ProjectRequireTagRepository projectRequireTagRepository;
    private final ProjectRepository projectRepository;
    private final TraitTableService traitTableService;

    // 성향, 성격 매칭 최종
    public Set<String> matchByProjectRequireAndTrait(List<Long> requireList, String projectId){

        String leader= projectRepository.findLeaderByProjectId(projectId);

        Set <String> tmp =redisTagService.getRequireMatching(requireList);

        Set <String> answer =traitTableService.getTraitsMatching(leader,tmp);

        if (answer.isEmpty()) {
            throw new IllegalStateException("적합한 인원이 없습니다. 기술 태그를 수정해주세요.");
        }

        return answer;


    }


}
