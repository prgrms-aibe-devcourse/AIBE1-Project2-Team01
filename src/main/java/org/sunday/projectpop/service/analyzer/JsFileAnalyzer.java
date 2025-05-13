package org.sunday.projectpop.service.analyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsFileAnalyzer implements SourceFileAnalyzer {
    @Override
    public boolean supports(String filename) {
        return filename.endsWith(".js")
                || filename.endsWith(".ts");
    }

    @Override
    public String analyze(String content) {
        // 클래스와 메서드 시그니처 추출
        StringBuilder result = new StringBuilder();
        Matcher classMatcher = Pattern.compile("class\\s+(\\w+)").matcher(content);
        while (classMatcher.find()) {
            result.append("클래스: ").append(classMatcher.group(1)).append("\n");
        }

        String regex = "function\\s+(\\w+)\\(";
        Matcher functionMatcher = Pattern.compile(regex).matcher(content);
        while (functionMatcher.find()) {
            result.append("함수: ").append(functionMatcher.group(1)).append("\n");
        }
        return result.toString();
    }
}
