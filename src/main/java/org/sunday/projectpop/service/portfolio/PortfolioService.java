package org.sunday.projectpop.service.portfolio;

import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.model.dto.PortfolioCreateRequest;
import org.sunday.projectpop.model.dto.PortfolioResponse;
import org.sunday.projectpop.model.entity.Portfolio;

import java.util.List;

public interface PortfolioService {
    void createPortfolio(String userId, PortfolioCreateRequest request, List<MultipartFile> files);

    List<Portfolio> getMyPortfolios(String userId) throws PortfolioNotFoundException;

    PortfolioResponse getPortfolio(String portfolioId);

    void updatePortfolio(String userId, String portfolioId, PortfolioCreateRequest request);

    void deletePortfolio(String userId, String portfolioId);
}
