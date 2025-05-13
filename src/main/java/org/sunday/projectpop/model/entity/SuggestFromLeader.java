package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name="suggest_from_leader")
@Entity
@Data
public class SuggestFromLeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserAccount sender;  // 리더

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserAccount receiver;  // 제안 대상 유저

    @Column(length = 1000)
    private String message;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean checking = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
