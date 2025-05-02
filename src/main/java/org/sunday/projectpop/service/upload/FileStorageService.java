package org.sunday.projectpop.service.upload;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
//    String upload(MultipartFile file) throws Exception;
    String uploadAndGenerateSignedUrl(MultipartFile file, int expirationSeconds) throws Exception;
}
