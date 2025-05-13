package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sunday.projectpop.model.entity.Project;
import org.sunday.projectpop.model.entity.SkillTag;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // ✅ 필수
@AllArgsConstructor
@Entity
@Table(name = "project_Selective_tag", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "tag_id"}))
@Data
public class ProjectSelectiveTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private SkillTag tag;

    public ProjectSelectiveTag(Project project, SkillTag tag) {
        this.project = project;
        this.tag = tag;
    }
}