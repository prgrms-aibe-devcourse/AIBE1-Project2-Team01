package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PortfolioUrl {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioFileId;

    private String url;
    private String description;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;
}
