package ru.clevertec.exception;

import ru.clevertec.util.Constants;

public class UnsupportCacheException extends RuntimeException {
    private UnsupportCacheException(String message) {
        super(message);
    }

    public static UnsupportCacheException getByType() {
        return new UnsupportCacheException(Constants.ERROR_CACHE_TYPE);
    }
}