package ru.clevertec.exception;

import ru.clevertec.util.Constants;

public class CommentNotFoundException extends RuntimeException {
    private CommentNotFoundException(String message) {
        super(message);
    }

    public static CommentNotFoundException getById(Long id) {
        return new CommentNotFoundException(Constants.ERROR_NO_SUCH_COMMENT + id);
    }
}