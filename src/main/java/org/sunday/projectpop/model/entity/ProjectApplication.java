package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectApplication {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private org.sunday.projectpop.model.entity.Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount user;

    private LocalDateTime appliedAt;

    public ProjectApplication(Project project, UserAccount user) {
        this.project = project;
        this.user = user;
        this.appliedAt = LocalDateTime.now();
    }
}