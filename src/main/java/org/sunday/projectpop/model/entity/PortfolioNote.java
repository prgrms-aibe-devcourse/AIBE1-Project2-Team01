package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Data
@Entity
public class PortfolioNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String portfolioId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 2000)
    private String content;

    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);
}