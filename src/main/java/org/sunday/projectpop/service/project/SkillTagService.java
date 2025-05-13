

package org.sunday.projectpop.service.project;



import org.sunday.projectpop.model.entity.SkillTag;

import java.util.List;

//공고 생성을 위해 임시 구현

public interface SkillTagService {
    List<SkillTag> getTagsByIds(List<Long> ids);
    List<SkillTag> getAllTags();

}
