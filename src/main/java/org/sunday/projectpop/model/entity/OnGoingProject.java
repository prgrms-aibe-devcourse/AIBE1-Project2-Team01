package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "ongoing_project")
public class OnGoingProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ongoing_project_id")
    private Long onGoingProjectId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "team_leader_id", length = 36)
    private String teamLeaderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'ONGOING'")
    private Status status = Status.ONGOING;

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
