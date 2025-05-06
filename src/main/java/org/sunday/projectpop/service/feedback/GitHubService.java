package org.sunday.projectpop.service.feedback;

import java.util.List;

public interface GitHubService {
    List<String> fetchAndConvertFiles(String url);
}
