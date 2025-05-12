package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioSummary;

@Repository
public interface PortfolioSummaryRepository extends JpaRepository<PortfolioSummary, Long> {

    PortfolioSummary findByPortfolio(Portfolio portfolio);

}

