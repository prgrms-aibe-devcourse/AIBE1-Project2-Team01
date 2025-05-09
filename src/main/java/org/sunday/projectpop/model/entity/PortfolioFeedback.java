package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sunday.projectpop.model.enums.AnalysisStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    private Long noteId; // 회고 노트와의 연결

    @Lob
    private String llmFeedback;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;

    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);
}