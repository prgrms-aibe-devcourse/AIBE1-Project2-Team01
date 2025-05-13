package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // ✅ 필수
@AllArgsConstructor
@Entity
@Table(name = "project_require_tag", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "tag_id"}))
@Data
public class ProjectRequireTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private SkillTag tag;

    public ProjectRequireTag(Project project, SkillTag tag) {
        this.project = project;
        this.tag = tag;
    }
}