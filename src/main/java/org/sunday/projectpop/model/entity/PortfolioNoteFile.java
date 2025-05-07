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
public class PortfolioNoteFile implements ReadableFile{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portfolioNoteFileId;

    private String originalFilename;
    @Lob
    private String storedUrl;
    private String storedFilename; // 삭제를 위한
    private String fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_note_id", nullable = false)
    private PortfolioNote portfolioNote;
}

