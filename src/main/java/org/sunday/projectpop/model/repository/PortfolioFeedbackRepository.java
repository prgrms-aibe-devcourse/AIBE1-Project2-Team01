package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.sunday.projectpop.model.dto.FeedbackResponse;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioFeedback;
import org.sunday.projectpop.model.entity.PortfolioNote;

import java.util.List;

@Repository
public interface PortfolioFeedbackRepository extends JpaRepository<PortfolioFeedback, Long> {

    PortfolioFeedback findByPortfolio(Portfolio portfolio);

    @Query("SELECT new org.sunday.projectpop.model.dto.FeedbackResponse(p.id, p.status, p.llmFeedback) " +
            "FROM PortfolioFeedback p WHERE p.portfolio = :portfolio")
    List<FeedbackResponse> findFeedbackAndStatusByPortfolio(@Param("portfolio") Portfolio portfolio);

    @Query("SELECT new org.sunday.projectpop.model.dto.FeedbackResponse(p.id, p.status, p.llmFeedback) " +
            "FROM PortfolioFeedback p WHERE p.id = :id")
    FeedbackResponse findFeedbackAndStatusById(@Param("id") Long id);

    @Query("SELECT new org.sunday.projectpop.model.dto.FeedbackResponse(p.id, p.status, p.llmFeedback) " +
            "FROM PortfolioFeedback p WHERE p.portfolio = :portfolio AND p.note = :note")
    FeedbackResponse findLatestFeedback(@Param("portfolio") Portfolio portfolio, @Param("note") PortfolioNote note);

    PortfolioFeedback findTopByPortfolioOrderByCreatedAtDesc(Portfolio portfolio);

    @Query(value = "SELECT pf.id, pf.created_at, pf.llm_feedback, pf.note_id, pf.portfolio_id, pf.status FROM portfolio_feedback pf WHERE pf.portfolio_id = :portfolioId ORDER BY pf.created_at DESC LIMIT 1 OFFSET 1", nativeQuery = true)
    PortfolioFeedback findSecondLatestByPortfolioId(@Param("portfolioId") String portfolioId);

}

