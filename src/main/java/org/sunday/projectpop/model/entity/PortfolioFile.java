package org.sunday.projectpop.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PortfolioFile implements ReadableFile{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioFileId;

    private String originalFilename;
    @Lob
    private String storedUrl;
    private String storedFilename; // 삭제를 위한
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;
}

