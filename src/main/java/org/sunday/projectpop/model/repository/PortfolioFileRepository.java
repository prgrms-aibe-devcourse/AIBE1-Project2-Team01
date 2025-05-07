package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioFile;

import java.util.List;

@Repository
public interface PortfolioFileRepository extends JpaRepository<PortfolioFile, Long> {

    List<PortfolioFile> findAllByPortfolio(Portfolio portfolio);
    List<PortfolioFile> findAllByPortfolioFileIdIn(List<Long> ids);

//    void delete(PortfolioFile file);
}
