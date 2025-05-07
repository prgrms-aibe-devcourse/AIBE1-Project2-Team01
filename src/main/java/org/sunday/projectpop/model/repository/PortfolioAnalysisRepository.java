package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioAnalysis;

@Repository
public interface PortfolioAnalysisRepository extends JpaRepository<PortfolioAnalysis, Long> {

    PortfolioAnalysis findByPortfolio(Portfolio portfolio);
}
