package ru.clevertec.exception;

import ru.clevertec.util.Constants;

public class NotMatchNewsCommentException extends RuntimeException {
    private NotMatchNewsCommentException(String message) {
        super(message);
    }

    public static NotMatchNewsCommentException getById(Long newsId, Long commentId) {
        return new NotMatchNewsCommentException(
                String.format(Constants.ERROR_NO_MACH_NEWS_COMMENT, newsId, commentId));
    }
}
