package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PortfolioFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioFileId;

    private String originalFilename;
    private String storedUrl;
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;
}
