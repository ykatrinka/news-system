package ru.clevertec.exception;

public class CommentIndexException extends RuntimeException {
    private CommentIndexException(String message) {
        super(message);
    }

    public static CommentIndexException getInstance(String message) {
        return new CommentIndexException(message);
    }
}