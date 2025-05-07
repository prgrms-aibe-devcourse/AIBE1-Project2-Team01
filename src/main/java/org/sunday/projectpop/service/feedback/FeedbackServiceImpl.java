package org.sunday.projectpop.service.feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.sunday.projectpop.exceptions.PortfolioNotFoundException;
import org.sunday.projectpop.model.entity.*;
import org.sunday.projectpop.model.repository.PortfolioFileRepository;
import org.sunday.projectpop.model.repository.PortfolioNoteFileRepository;
import org.sunday.projectpop.model.repository.PortfolioRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class FeedbackServiceImpl implements FeedbackService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioFileRepository portfolioFileRepository;
    private final PortfolioNoteFileRepository portfolioNoteFileRepository;
    private final GitHubService gitHubService;
    private final FileReadService fileReadService;

    @Override
    public void generateFeedback(String id) {
        // 포트폴리오 있는지 확인
        Portfolio portfolio = findById(id);

        List<String> gitHubTexts = extractGithubTexts(portfolio);
        log.info("gitHubTexts = " + gitHubTexts.toString());

        // 업로드 파일 변환 처리
        List<ReadableFile> allFiles = new ArrayList<>();
        // 파일이 있는지 확인
        List<PortfolioFile> portfolioFiles = portfolioFileRepository.findAllByPortfolio(portfolio);
        if (!portfolioFiles.isEmpty()) {
            allFiles.addAll(portfolioFiles);
        }

        // 노트가 있는지 확인
        if (!portfolio.getNotes().isEmpty()) {
            List<PortfolioNote> portfolioNotes = portfolio.getNotes();
            List<PortfolioNoteFile> noteFiles = new ArrayList<>();
            for (PortfolioNote note : portfolioNotes) {
                List<PortfolioNoteFile> portfolioNoteFiles = portfolioNoteFileRepository.findAllByPortfolioNoteId(note.getId());
                noteFiles.addAll(portfolioNoteFiles);
            }
            allFiles.addAll(noteFiles);
        }

        // 파일에서 텍스트 추출
        List<String> fileTexts = fileReadService.extractTextsFromFiles(allFiles);
        log.info("fileTextx = " + fileTexts.toString());


        // TODO: LLM에 전달할 형태로 합치기
        // TODO: LLM에 전달
        // TODO: DB 저장 (feedback)
    }



    private List<String> extractGithubTexts(Portfolio portfolio) {
        List<PortfolioUrl> urls = portfolio.getUrls();

        // urls가 바이었다면 바로 빈 리스트 반환
        if (urls == null || urls.isEmpty()) {
            return Collections.emptyList();
        }

        // github.com이 포함된 URL 찾기
        for (PortfolioUrl url : urls) {
            String link = url.getUrl();
            if (link != null && link.contains("github.com")) {
                return gitHubService.fetchAndConvertFiles(link);
                // 현재는 하나의 링크만 처리
            }
        }

        // 없다면 빈 리스트 반환
        return Collections.emptyList();
    }

    private Portfolio findById(String id) {
        return portfolioRepository.findById(id).orElseThrow(
                () -> new PortfolioNotFoundException("해당 포트폴리오를 찾을 수 없습니다.")
        );
    }
}
