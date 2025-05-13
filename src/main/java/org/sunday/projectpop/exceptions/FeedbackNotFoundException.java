package org.sunday.projectpop.exceptions;

public class FeedbackNotFoundException extends RuntimeException{
    public FeedbackNotFoundException(String message) {
        super(message);
    }
}
