package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sunday.projectpop.model.enums.AnalysisStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Lob
    private String githubSummary;

    @Lob
    private String fileSummary;

    @Lob
    private String finalSummary;

    @Lob
    private String llmFeedback;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus summaryStatus;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus feedbackStatus;

    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);

}
