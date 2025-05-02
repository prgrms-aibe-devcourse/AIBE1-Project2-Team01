package org.sunday.projectpop.project.model.service;



import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.sunday.projectpop.project.model.entity.SkillTag;
import org.sunday.projectpop.project.model.repository.SkillTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SkillTagService {
    List<SkillTag> getTagsByIds(List<Long> ids);
    List<SkillTag> getAllTags();
}
