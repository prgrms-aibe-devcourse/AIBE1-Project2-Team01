package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sunday.projectpop.model.enums.PortfolioType;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String portfolioId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PortfolioType portfolioType;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String description;

    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioUrl> urls = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioNote> notes = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioAnalysis> analyses = new ArrayList<>();
}
