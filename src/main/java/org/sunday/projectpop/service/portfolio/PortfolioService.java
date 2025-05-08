package org.sunday.projectpop.service.portfolio;

import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.model.dto.*;

import java.util.List;

public interface PortfolioService {
    void createPortfolio(String userId, PortfolioRequest request);

    List<PortfolioSimple> getMyPortfolios(String userId) throws PortfolioNotFoundException;

    PortfolioResponse getPortfolio(String portfolioId);

    void updatePortfolio(String userId, String portfolioId, PortfolioUpdateRequest request, List<MultipartFile> newFiles);

    void deletePortfolio(String userId, String portfolioId);

}
