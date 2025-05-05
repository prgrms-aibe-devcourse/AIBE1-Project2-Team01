package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioNote;

import java.util.List;

public interface PortfolioNoteRepository extends JpaRepository<PortfolioNote, Long> {
    List<PortfolioNote> findAllByPortfolio(Portfolio portfolio);
}
