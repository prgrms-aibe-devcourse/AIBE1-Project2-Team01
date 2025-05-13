package org.sunday.projectpop.service.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.SkillTag;
import org.sunday.projectpop.model.repository.SkillTagRepository;

import java.util.List;

//공고 생성을 위해 임시 구현
@Service
@RequiredArgsConstructor
public class SkillTagServiceImpl implements SkillTagService {

    private final SkillTagRepository skillTagRepository;

    @Override
    public List<SkillTag> getAllTags() {
        return skillTagRepository.findAll();
    }

    @Override
    public List<SkillTag> getTagsByIds(List<Long> ids) {
        return skillTagRepository.findAllById(ids);
    }
}
