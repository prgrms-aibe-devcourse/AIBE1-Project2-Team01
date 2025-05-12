package org.sunday.projectpop.service.analyzer;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CodeAnalyzer {

    private final List<SourceFileAnalyzer> analyzers = List.of(
            new JavaFileAnalyzer(),
            new PythonFileAnalyzer(),
            new JsFileAnalyzer()
    );

    public Optional<SourceFileAnalyzer> getAnalyzer(String filename) {
        return analyzers.stream()
                .filter(analyzer -> analyzer.supports(filename))
                .findFirst();
    }

    public String analyzeFile(String filename, String content) {
        return getAnalyzer(filename)
                .map(analyzer -> analyzer.analyze(content))
                .orElse("지원되지 않는 파일: " + filename);
    }
}
