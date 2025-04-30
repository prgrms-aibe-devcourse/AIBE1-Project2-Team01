package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.sunday.projectpop.model.enums.PortfoliosType;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Entity
@Data
public class Portfolio {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String portfolioId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PortfoliosType portfolioType;

    @Column(nullable = false)
    private String url;

    @Column(length = 2000, nullable = false)
    private String description;

    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);

    @Column(nullable = false)
    private boolean fromHere;

    private String linkedProjectId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "linked_project_id")
//    private Project linkedProject;
}
