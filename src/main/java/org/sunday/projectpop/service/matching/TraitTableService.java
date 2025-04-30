package org.sunday.projectpop.service.matching;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.dto.OCEAN;
import org.sunday.projectpop.model.repository.ProjectRepository;
import org.sunday.projectpop.model.repository.UserSkillTagRepository;
import org.sunday.projectpop.model.repository.UserTraitRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TraitTableService {
    private final ProjectRepository projectRepository;
    private final UserTraitRepository userTraitRepository;

    public Set<String> getTraitsMatching(@Param("user_id") User leader, int ranges ) {
        OCEAN ocean;



        return Set.of();
    }
}
