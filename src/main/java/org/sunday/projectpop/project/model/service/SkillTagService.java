

package org.sunday.projectpop.project.model.service;



import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.sunday.projectpop.project.model.entity.SkillTag;
import org.sunday.projectpop.project.model.repository.SkillTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

//공고 생성을 위해 임시 구현

public interface SkillTagService {
    List<SkillTag> getTagsByIds(List<Long> ids);
    List<SkillTag> getAllTags();

}
