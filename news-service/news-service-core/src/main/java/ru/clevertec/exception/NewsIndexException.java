package ru.clevertec.exception;

public class NewsIndexException extends RuntimeException {
    private NewsIndexException(String message) {
        super(message);
    }

    public static NewsIndexException getInstance(String message) {
        return new NewsIndexException(message);
    }
}