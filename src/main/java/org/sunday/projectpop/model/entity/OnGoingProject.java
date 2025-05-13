package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "ongoing_project")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnGoingProject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id") // 이 부분이 누락됨
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "team_leader_id", length = 36)
    private String teamLeaderId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public enum Status {
        ONGOING,
        COMPLETED,
        CANCELLED
    }
}
