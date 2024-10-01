package ru.clevertec.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final int COMMENTS_PAGE_SIZE = 4;
    public static final String ERROR_NO_SUCH_COMMENT = "No such Comment with id ";
    public static final String ERROR_NO_SUCH_NEWS = "No such News with id ";
    public static final String ERROR_INVALID_CLASS = "Invalid class %s";
    public static final String ERROR_INDEX_INTERRUPTED = "Index interrupted";
}
