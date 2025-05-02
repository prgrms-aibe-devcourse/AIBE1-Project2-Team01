package org.sunday.projectpop.service.upload;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileStorageService {
//    String upload(MultipartFile file) throws Exception;
    Map<String, String> uploadAndGenerateSignedUrl(MultipartFile file, int expirationSeconds) throws Exception;

    void deleteFile(String filename) throws Exception;
}
