package org.sunday.projectpop.service.feedback;

import java.time.Instant;
import java.util.List;

public interface GitHubService {
    List<String> fetchAndConvertFiles(String url);

    Instant fetchUpdatedAtFromGithub(String githubUrl);
}