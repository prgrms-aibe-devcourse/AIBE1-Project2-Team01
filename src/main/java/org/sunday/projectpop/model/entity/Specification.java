package org.sunday.projectpop.model.entity; // 'package'를 'pkg'로 변경
//
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "specification")
public class Specification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specification_id")
    private Long specificationId;

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

    @Column(name = "progress_rate", nullable = false)
    private Integer progressRate;

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


}