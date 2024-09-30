package ru.clevertec.exception;

import ru.clevertec.util.Constants;

public class NewsNotFoundException extends RuntimeException {
    private NewsNotFoundException(String message) {
        super(message);
    }

    public static NewsNotFoundException getById(Long id) {
        return new NewsNotFoundException(Constants.ERROR_NO_SUCH_NEWS + id);
    }
}