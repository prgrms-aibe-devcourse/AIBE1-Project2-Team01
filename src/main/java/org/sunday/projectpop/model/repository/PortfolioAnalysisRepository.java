package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.sunday.projectpop.model.dto.FeedbackResponse;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioAnalysis;

import java.util.List;

@Repository
public interface PortfolioAnalysisRepository extends JpaRepository<PortfolioAnalysis, Long> {

    PortfolioAnalysis findByPortfolio(Portfolio portfolio);

    @Query("SELECT new org.sunday.projectpop.model.dto.FeedbackResponse(p.id, p.feedbackStatus, p.llmFeedback) " +
            "FROM PortfolioAnalysis p WHERE p.portfolio = :portfolio")
    List<FeedbackResponse> findFeedbackAndStatusByPortfolio(@Param("portfolio") Portfolio portfolio);
}

