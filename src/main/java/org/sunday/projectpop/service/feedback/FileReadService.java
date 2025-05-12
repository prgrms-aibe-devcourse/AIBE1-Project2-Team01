package org.sunday.projectpop.service.feedback;

import org.sunday.projectpop.model.entity.ReadableFile;

import java.util.List;

public interface FileReadService {
    List<String> extractTextsFromFiles(List<ReadableFile> files);
}

