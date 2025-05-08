package org.sunday.projectpop.project.model.repository;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.sunday.projectpop.project.model.dto.ProjectSearchCondition;
import org.sunday.projectpop.project.model.entity.Project;
import org.sunday.projectpop.project.model.entity.SkillTag;

import java.util.ArrayList;
import java.util.List;

public class ProjectSpecification {

    public static Specification<Project> search(ProjectSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 🔍 키워드 검색
            if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
                String keyword = "%" + condition.getKeyword().toLowerCase() + "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("description")), keyword),
                        cb.like(cb.lower(root.get("field")), keyword),
                        cb.like(cb.lower(root.get("type")), keyword),
                        cb.like(cb.lower(root.get("status")), keyword),
                        cb.like(cb.lower(root.get("locationType")), keyword)
                ));
            }



            // ✅ 다중 선택 필터
            if (!CollectionUtils.isEmpty(condition.getType())) {
                predicates.add(root.get("type").in(condition.getType()));
            }

            if (!CollectionUtils.isEmpty(condition.getField())) {
                predicates.add(root.get("field").in(condition.getField()));
            }

            if (!CollectionUtils.isEmpty(condition.getLocationType())) {
                predicates.add(root.get("locationType").in(condition.getLocationType()));
            }

            if (!CollectionUtils.isEmpty(condition.getStatus())) {
                predicates.add(root.get("status").in(condition.getStatus()));
            }
            // skillTag 조건: Project -> skillTags 조인 후 name으로 필터
//            if (!CollectionUtils.isEmpty(condition.getSkillTag())) {
//                predicates.add(root.join("skillTags").get("name").in(condition.getSkillTag()));
//            }
            if (!CollectionUtils.isEmpty(condition.getSkillTag())) {
                Join<Project, SkillTag> tagJoin = root.join("skillTags", JoinType.INNER);
                predicates.add(tagJoin.get("name").in(condition.getSkillTag()));

            }
            query.distinct(true); // 중복 제거


// experienceLevel 조건: String 리스트 그대로 필터
            if (!CollectionUtils.isEmpty(condition.getExperienceLevel())) {
                predicates.add(root.get("experienceLevel").in(condition.getExperienceLevel()));
            }


            // 🔃 정렬
//            if ("최신순".equalsIgnoreCase(condition.getSortBy())) {
//                query.orderBy(cb.desc(root.get("createdAt")));
//            } else if ("오래된순".equalsIgnoreCase(condition.getSortBy())) {
//                query.orderBy(cb.asc(root.get("createdAt")));
//            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
