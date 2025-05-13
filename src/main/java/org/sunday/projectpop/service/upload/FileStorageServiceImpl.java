package org.sunday.projectpop.service.upload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.sunday.projectpop.exceptions.FileManagementException;
import org.sunday.projectpop.model.entity.Portfolio;
import org.sunday.projectpop.model.entity.PortfolioFile;
import org.sunday.projectpop.model.entity.PortfolioNote;
import org.sunday.projectpop.model.entity.PortfolioNoteFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
@Log
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${supabase.url}")
    private String url;

    @Value("${supabase.access-key}")
    private String accessKey;

    @Value("${supabase.bucket-name}")
    private String bucketName;


    @Override
    public PortfolioFile uploadPortfolioFile(MultipartFile file, Portfolio portfolio) {
       try {
           Map<String, String> map = uploadAndGenerateSignedUrl(file, 3600);
           return PortfolioFile.builder()
                   .fileType(file.getContentType())
                   .originalFilename(file.getOriginalFilename())
                   .storedUrl(map.get("signedUrl"))
                   .storedFilename(map.get("filename"))
                   .portfolio(portfolio)
                   .build();
       } catch (Exception e) {
           log.severe(e.getMessage());
           throw new FileManagementException("파일 업로드에 실패했습니다.");
       }
    }

    @Override
    public PortfolioNoteFile uploadPortfolioNoteFile(MultipartFile file, PortfolioNote portfolioNote) {
        try {
            Map<String, String> map = uploadAndGenerateSignedUrl(file, 3600);
            return PortfolioNoteFile.builder()
                    .fileType(file.getContentType())
                    .originalFilename(file.getOriginalFilename())
                    .storedUrl(map.get("signedUrl"))
                    .storedFilename(map.get("filename"))
                    .portfolioNote(portfolioNote)
                    .build();
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new FileManagementException("파일 업로드에 실패했습니다.");
        }
    }

    @Override
    public Map<String, String> uploadAndGenerateSignedUrl(MultipartFile file, int expirationSeconds) throws Exception {
        String filename = upload(file);
        Map<String, String> result = new HashMap<>();
        result.put("filename", filename);
        result.put("signedUrl", generateSignedUrl(filename, expirationSeconds));
        return result;
    }

    @Override
    public void deleteFile(String filename) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("%s/storage/v1/object/%s/%s"
                        .formatted(url, bucketName, filename)))
                .header("Authorization", "Bearer %s".formatted(accessKey))
                .DELETE()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        log.info(String.valueOf(response.statusCode()));
        if (response.statusCode() != 200) {
            throw new FileManagementException("파일 삭제 실패: " + response.body());
        }
        log.info("파일 삭제 성공: " + filename);
    }

    private String upload(MultipartFile file) throws Exception {
        String uuid = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
        String boundary = "Boundary-%s".formatted(uuid);
        String filename = "%s.%s".formatted(uuid, extension);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("%s/storage/v1/object/%s/%s"
                        .formatted(url, bucketName, filename)))
                .header("Authorization", "Bearer %s".formatted(accessKey))
                .header("Content-Type", "multipart/form-data; boundary=%s".formatted(boundary))
                .POST(ofMimeMultipartData(file, boundary))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new FileManagementException("파일 업로드 실패: " + response.body());
        }
//        log.info("filename: " + filename);
        return filename;
    }

    private HttpRequest.BodyPublisher ofMimeMultipartData(MultipartFile file, String boundary) throws IOException {
        List<byte[]> byteArrays = List.of(
                ("--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getOriginalFilename() + "\"\r\n" +
                        "Content-Type: " + file.getContentType() + "\r\n\r\n").getBytes(),
                file.getBytes(),
                ("\r\n--" + boundary + "--\r\n").getBytes()
        );
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }

    @Override
    public String generateSignedUrl(String filename, int expirationSeconds) throws IOException, InterruptedException {
        String jsonBody = """
                {
                    "expiresIn": %d
                }
                """.formatted(expirationSeconds);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("%s/storage/v1/object/sign/%s/%s"
                        .formatted(url, bucketName, filename)))
                .header("Authorization", "Bearer %s".formatted(accessKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new FileManagementException("파일 URL 생성 실패: " + response.body());
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.body());
        String signedURL = jsonNode.get("signedURL").asText();
        log.info("signedUrl: " + signedURL);
        return "%s/storage/v1%s".formatted(url, signedURL);
    }

}
