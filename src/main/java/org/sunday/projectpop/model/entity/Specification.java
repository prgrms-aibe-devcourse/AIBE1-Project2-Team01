package org.sunday.projectpop.model.entity; // 'package'를 'pkg'로 변경
//
import jakarta.persistence.*;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "specification")
@Data
@AllArgsConstructor
@Builder
public class Specification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id") // 이 부분이 누락됨
    private String id;

    @ManyToOne
    @JoinColumn(name = "ongoing_project_id", nullable = false)
    private OnGoingProject onGoingProject;

    @Column(name = "requirement", nullable = false, columnDefinition = "TEXT")
    private String requirement;

    @Column(name = "assignee")
    private String assignee;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public Specification() {} // 기본 생성자 추가


}