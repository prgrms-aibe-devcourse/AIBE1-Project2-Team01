package org.sunday.projectpop.service.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.model.entity.ReadableFile;
import org.sunday.projectpop.service.upload.FileStorageService;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
@Log
@RequiredArgsConstructor
public class FileReadServiceImpl implements FileReadService {

    private final FileStorageService fileStorageService;

    @Override
    public List<String> extractTextsFromFiles(List<ReadableFile> files) {
        return files.stream()
//                .map(this::extractFormattedText)
                .map(this::extractTextFromTika)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
                .toList();
    }

    private String extractTextFromTika(ReadableFile file) {
//        String extension = file.getFileType().toLowerCase();

        AutoDetectParser parser = new AutoDetectParser();
        try (InputStream input = new URL(fileStorageService.generateSignedUrl(file.getStoredFilename(), 3600)).openStream()) {
            BodyContentHandler handler =new BodyContentHandler(-1); // 무제한
            Metadata metadata = new Metadata();
            parser.parse(input, handler, metadata, new ParseContext());
            return handler.toString();
        } catch (Exception e) {
            return "";
        }

    }

    /*
    private Optional<String> extractFormattedText(ReadableFile file) {
        String extension = file.getFileType().toLowerCase();

        try (InputStream input = new URL(fileStorageService.generateSignedUrl(file.getStoredFilename(), 3600)).openStream()) {
            String content = switch (extension) {
                case "txt" -> extractFromTxt(input);
                case "pdf" -> extractFromPdf(input);
                case "docx", "doc" -> extractFromDoc(input);
//                case "png", "jpg", "jpeg" -> extractFromImage(input);
//                case "ppt", "pptx" -> extractFromPpt(input);
//                case "xlsx", "xls" -> extractFromExcel(input);
                default -> null;
            };

            if (content == null || content.isBlank()) return Optional.empty();
            return Optional.of("[%s] : %s".formatted(file.getOriginalFilename(), content.trim()));

        } catch (Exception e) {
            log.severe(e.getMessage());
            return Optional.empty();
        }
    }

    private String extractFromTxt(InputStream input) throws IOException {
        return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String extractFromPdf(InputStream input) throws IOException {
        return null;
    }

    private String extractFromDoc(InputStream input) throws IOException {
        return null;
    }

    private String extractFromImage(InputStream input) throws IOException {
        return null;
    }

    private String extractFromPpt(InputStream input) throws IOException {
        return null;
    }

    private String extractFromExcel(InputStream input) throws IOException {
        return null;
    }
*/
}
