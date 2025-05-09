package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sunday.projectpop.model.enums.AnalysisStatus;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "summary") // Portfolio 엔티티의 'summary' 필드와 매핑
    private Portfolio portfolio;

    @Lob
    private String githubSummary;

    @Lob
    private String fileSummary;

    @Lob
    private String finalSummary;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;

    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);
}