package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserAccount sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserAccount receiver;

    @Column(length = 1000)
    private String content;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean checking = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt = LocalDateTime.now();
}
