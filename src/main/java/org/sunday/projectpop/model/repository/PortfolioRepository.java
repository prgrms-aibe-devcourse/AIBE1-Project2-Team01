package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunday.projectpop.model.entity.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, String> {
}
