package org.sunday.projectpop.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunday.projectpop.model.entity.PortfolioUrl;

import java.util.List;

@Repository
public interface PortfolioUrlRepository extends JpaRepository<PortfolioUrl, Long> {
    List<PortfolioUrl> findAllByPortfolioUrlIdIn(List<Long> ids);

//    void deleteAll(List<PortfolioUrl> urls);
}
