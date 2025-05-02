package org.sunday.projectpop.exceptions;

public class FileUploadFailureException extends RuntimeException {
    public FileUploadFailureException(String message) {
        super(message);
    }
}
