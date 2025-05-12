package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunday.projectpop.model.entity.PortfolioNoteFile;

import java.util.List;

@Repository
public interface PortfolioNoteFileRepository extends JpaRepository<PortfolioNoteFile, Long> {

    List<PortfolioNoteFile> findAllByPortfolioNoteFileIdIn(List<Long> ids);

    List<PortfolioNoteFile> findAllByPortfolioNoteId(Long portfolioNoteId);

}
