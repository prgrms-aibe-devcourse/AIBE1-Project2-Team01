package org.sunday.projectpop.service.analyzer;

public interface SourceFileAnalyzer {
    boolean supports(String filename);
    String analyze(String content);
}
