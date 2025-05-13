

package org.sunday.projectpop.service.service;



import java.util.List;

//공고 생성을 위해 임시 구현

public interface SkillTagService {
    List<SkillTag> getTagsByIds(List<Long> ids);
    List<SkillTag> getAllTags();

}
