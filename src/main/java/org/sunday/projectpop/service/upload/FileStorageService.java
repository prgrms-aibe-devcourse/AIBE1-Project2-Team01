package org.sunday.projectpop.service.upload;

import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioFile;
import org.sunday.projectpop.model.entity.PortfolioNote;
import org.sunday.projectpop.model.entity.PortfolioNoteFile;

import java.io.IOException;
import java.util.Map;

public interface FileStorageService {
//    String upload(MultipartFile file) throws Exception;

    Map<String, String> uploadAndGenerateSignedUrl(MultipartFile file, int expirationSeconds) throws Exception;

    void deleteFile(String filename) throws Exception;

    PortfolioFile uploadPortfolioFile(MultipartFile file, Portfolio portfolio);

    PortfolioNoteFile uploadPortfolioNoteFile(MultipartFile file, PortfolioNote portfolioNote
    );

    String generateSignedUrl(String filename, int expirationSeconds) throws IOException, InterruptedException;
}
