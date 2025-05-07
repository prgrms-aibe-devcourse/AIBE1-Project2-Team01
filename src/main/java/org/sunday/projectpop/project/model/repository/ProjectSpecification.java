package org.sunday.projectpop.project.model.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.sunday.projectpop.project.model.dto.ProjectSearchCondition;
import org.sunday.projectpop.project.model.entity.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectSpecification {

    public static Specification<Project> search(ProjectSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
                String keyword = "%" + condition.getKeyword().toLowerCase() + "%";

                // 여러 컬럼에 대해 LIKE 검색
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("description")), keyword),
                        cb.like(cb.lower(root.get("field")), keyword),
                        cb.like(cb.lower(root.get("type")), keyword),
                        cb.like(cb.lower(root.get("status")), keyword),
                        cb.like(cb.lower(root.get("locationType")), keyword)
                ));
            }

            // 나머지 필터는 선택적으로 추가
            if (condition.getType() != null && !condition.getType().isBlank()) {
                predicates.add(cb.equal(root.get("type"), condition.getType()));
            }

            if (condition.getField() != null && !condition.getField().isBlank()) {
                predicates.add(cb.equal(root.get("field"), condition.getField()));
            }

            if (condition.getLocationType() != null && !condition.getLocationType().isBlank()) {
                predicates.add(cb.equal(root.get("locationType"), condition.getLocationType()));
            }

            if (condition.getStatus() != null && !condition.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), condition.getStatus()));
            }
            if ("최신순".equalsIgnoreCase(condition.getSortBy())) {
                query.orderBy(cb.desc(root.get("createdAt")));
            } else if ("오래된 순".equalsIgnoreCase(condition.getSortBy())) {
                query.orderBy(cb.asc(root.get("createdAt")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
