package ru.clevertec.exception;

import ru.clevertec.util.Constants;

public class NewsNotExistsException extends RuntimeException {
    private NewsNotExistsException(String message) {
        super(message);
    }

    public static NewsNotExistsException existsById(Long id) {
        return new NewsNotExistsException(Constants.ERROR_NO_SUCH_NEWS + id);
    }
}