//package org.sunday.projectpop.temp.user;
//
//import org.sunday.projectpop.project.model.entity.SkillTag;
//import org.sunday.projectpop.project.model.service.SkillTagService;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class FakeSkillTagService implements SkillTagService {
//
//    @Override
//    public List<SkillTag> getTagsByIds(List<Long> ids) {
//        return ids.stream()
//                .map(id -> SkillTag.builder()
//                        .tagId(id)
//                        .name("테스트 태그 " + id)
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<SkillTag> getAllTags() {
//        return List.of(
//                SkillTag.builder().tagId(1L).name("Java").build(),
//                SkillTag.builder().tagId(2L).name("Spring").build()
//        );
//    }
//}