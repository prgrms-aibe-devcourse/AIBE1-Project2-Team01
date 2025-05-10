package org.sunday.projectpop.service.analyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonFileAnalyzer implements SourceFileAnalyzer {
    @Override
    public boolean supports(String filename) {
        return filename.endsWith(".py");
    }

    @Override
    public String analyze(String content) {
        // 클래스와 메서드 시그니처 추출
        StringBuilder result = new StringBuilder();
        Matcher classMatcher = Pattern.compile("class\\s+(\\w+)").matcher(content);
        while (classMatcher.find()) {
            result.append("클래스: ").append(classMatcher.group(1)).append("\n");
        }

        Matcher methodMatcher = Pattern.compile("def\\s+(\\w+)\\(").matcher(content);
        while (methodMatcher.find()) {
            result.append("메서드: ").append(methodMatcher.group(1)).append("\n");
        }
        return result.toString();
    }
}
