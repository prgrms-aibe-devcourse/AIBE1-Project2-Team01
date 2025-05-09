package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class PortfolioNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 2000)
    private String content;

    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);

    @OneToMany(mappedBy = "portfolioNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioNoteFile> files = new ArrayList<>();

     @OneToOne(mappedBy = "note", cascade = CascadeType.ALL)
    private PortfolioFeedback feedback; // 해당 노트에 대한 피드백
}