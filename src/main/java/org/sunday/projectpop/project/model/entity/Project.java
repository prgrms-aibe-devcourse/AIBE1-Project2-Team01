package org.sunday.projectpop.project.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Entity
@Table(name = "project")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Project {

    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount leader;

    @Column(length = 50, nullable = false)
    private String type; // 예: "PROJECT", "COMPETITION" (프론트에서 제한)

    @Column(name = "status", length = 50)
    private String status = "모집중"; // 모집중, 진행중, 완료 등 (프론트 제한)

    @Column(name = "generated_by_ai")
    private Boolean generatedByAi = false;

    @Column(length = 50)
    private String field;  // ENUM 제거, 프론트 제한

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_type", length = 50)
    private String locationType;  //

    @Column(name = "duration_weeks")
    private Integer durationWeeks;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
