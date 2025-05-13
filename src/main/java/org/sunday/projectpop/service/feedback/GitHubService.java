package org.sunday.projectpop.service.feedback;

import org.sunday.projectpop.model.dto.GitHubSummaryDTO;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface GitHubService {
    Mono<GitHubSummaryDTO> summarizeProject(String url);

    boolean isAfterLastCommit(String url, Instant time);
}