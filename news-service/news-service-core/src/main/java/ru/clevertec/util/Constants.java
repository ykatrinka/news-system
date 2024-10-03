package ru.clevertec.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final int NEWS_PAGE_SIZE = 4;

    public static final String ERROR_NO_SUCH_COMMENT = "No such Comment with id ";
    public static final String ERROR_NO_SUCH_NEWS = "No such News with id ";
    public static final String ERROR_NO_MACH_NEWS_COMMENT = "No match News with id %s and comment with id %s";
    public static final String ERROR_INVALID_CLASS = "Invalid class %s";
    public static final String ERROR_INDEX_INTERRUPTED = "Index interrupted";
    public static final String ERROR_CACHE_TYPE = "Cache type not supported";
    public static final String LOG_MESSAGE_BEFORE = "Received call :: method {} parameters {}";
    public static final String LOG_MESSAGE_AFTER_RETURNING = "Returning call :: method {} parameters {}";
    public static final String LOG_MESSAGE_AFTER_THROWING = "Throw exception :: method {} message {}";
    public static final String EMPTY_STRING = "";
}
