package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunday.projectpop.model.entity.PortfolioNote;

public interface PortfolioNoteRepository extends JpaRepository<PortfolioNote, Long> {
}
