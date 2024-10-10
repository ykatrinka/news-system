package util;

import ru.clevertec.dto.request.CommentRequest;
import ru.clevertec.dto.response.CommentResponse;

import java.time.LocalDateTime;
import java.util.List;

public class CommentTestData {

    public static final String USERNAME = "Patrik";
    public static final long NEWS_ID = 1L;
    public static final String TEXT = "This is a comment";
    public static final LocalDateTime CREATED_DATE = LocalDateTime
            .of(2024, 9, 3, 0, 0);
    public static final long COMMENT_ID = 1L;

    public static final int SEARCH_LIMIT = 10;
    public static final String SEARCH_VALUE = "Sarah";
    public static final List<String> SEARCH_FIELDS = List.of("username", "text");
    public static final List<String> SEARCH_NOT_VALID_FIELDS = List.of("title", "text");
    public static final String[] SEARCH_FIELDS_ARRAY = {"username", "text"};
    public static final String[] SEARCH_NOT_VALID_FIELDS_ARRAY = {"title", "text"};

    //integration
    public static final long COMMENT_ID_NEW = 5L;
    public static final long COMMENT_NEWS_ID = 1L;
    public static final String COMMENT_TEXT = "Comment 1";
    public static final long COMMENT_ID_NOT_FOUND = 7L;
    public static final long NEWS_ID_NO_CONTENT = 3L;
    public static final long NEWS_ID_NOT_FOUND = 5L;
    public static final long COMMENT_ID_UPD = 2L;


    public static CommentRequest getCommentRequest() {
        return CommentRequest.builder()
                .time(null)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT)
                .build();
    }

    public static CommentResponse getCommentResponse() {
        return CommentResponse.builder()
                .id(COMMENT_ID)
                .time(CREATED_DATE)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT)
                .build();
    }

    public static List<CommentResponse> getListWithTwoCommentResponse() {
        return List.of(
                CommentResponse.builder()
                        .id(1L)
                        .time(CREATED_DATE)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT)
                        .build(),
                CommentResponse.builder()
                        .id(2L)
                        .time(CREATED_DATE)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT)
                        .build()
        );
    }
}
