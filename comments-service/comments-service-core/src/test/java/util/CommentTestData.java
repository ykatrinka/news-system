package util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.clevertec.dto.request.CommentRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CommentTestData {

    public static final String USERNAME = "Patrik";
    public static final long NEWS_ID = 1L;
    public static final String TEXT_COMMENT = "This is a comment";

    public static final LocalDateTime CREATED_DATE = LocalDateTime
            .of(2024, 9, 3, 0, 0, 0);
    public static final int COMMENTS_PAGE_SIZE = 4;
    public static final int PAGE_NUMBER = 1;

    public static final long COMMENT_ID_FOR_GET = 7L;

    public static final long COMMENT_ID_FOR_UPDATE = 5L;
    public static final LocalDateTime UPDATED_DATE = LocalDateTime
            .of(2024, 8, 15, 0, 0, 0);
    public static final String TEXT_COMMENT_UPDATE = "This is a updated comment";

    public static final long NEWS_ID_FOR_GET = 1L;
    public static final long COMMENT_ID_FOR_DELETE = 3L;

    public static final String TEXT_SEARCH = "This is a Sarah comment";
    public static final String USERNAME_SEARCH = "Sarah";
    public static final String SEARCH_VALUE = "Sarah";
    public static final List<String> SEARCH_FIELDS = List.of("username", "text");
    public static final List<String> SEARCH_NOT_VALID_FIELDS = List.of("title", "text");
    public static final String[] SEARCH_FIELDS_ARRAY = {"username", "text"};

    //  create
    public static CommentRequest getCommentRequestForCreate() {
        return CommentRequest.builder()
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT)
                .build();
    }

    public static Comment getCommentForCreate() {
        return Comment.builder()
                .id(null)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT)
                .build();
    }

    public static Comment getFillCommentForCreate() {
        return Comment.builder()
                .id(1L)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT)
                .time(CREATED_DATE)
                .build();
    }

    public static CommentResponse getCommentResponseForCreate() {
        return CommentResponse.builder()
                .id(1L)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT)
                .time(CREATED_DATE)
                .build();
    }

    //  get all
    public static Page<Comment> getPageableListComments() {
        List<Comment> comments = List.of(
                Comment.builder()
                        .id(1L)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT)
                        .time(CREATED_DATE)
                        .build(),
                Comment.builder()
                        .id(2L)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT)
                        .time(CREATED_DATE)
                        .build(),
                Comment.builder()
                        .id(3L)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT)
                        .time(CREATED_DATE)
                        .build(),
                Comment.builder()
                        .id(4L)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT)
                        .time(CREATED_DATE)
                        .build()
        );
        return new PageImpl<>(comments);
    }

    public static List<CommentResponse> getListCommentsResponse() {
        return List.of(
                CommentResponse.builder()
                        .id(1L)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT)
                        .time(CREATED_DATE)
                        .build(),
                CommentResponse.builder()
                        .id(2L)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT)
                        .time(CREATED_DATE)
                        .build(),
                CommentResponse.builder()
                        .id(3L)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT)
                        .time(CREATED_DATE)
                        .build(),
                CommentResponse.builder()
                        .id(4L)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT)
                        .time(CREATED_DATE)
                        .build()
        );
    }

    //  get by id
    public static Comment getFillCommentForGetById() {
        return Comment.builder()
                .id(COMMENT_ID_FOR_GET)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT)
                .time(CREATED_DATE)
                .build();
    }

    public static CommentResponse getFillCommentResponseForGetById() {
        return CommentResponse.builder()
                .id(COMMENT_ID_FOR_GET)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT)
                .time(CREATED_DATE)
                .build();
    }

    //  update
    public static CommentRequest getCommentRequestForUpdate() {
        return CommentRequest.builder()
                .time(UPDATED_DATE)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT_UPDATE)
                .build();
    }

    public static Optional<Comment> getCommentForUpdate() {
        return Optional.of(
                Comment.builder()
                        .time(UPDATED_DATE)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT_UPDATE)
                        .build()
        );
    }

    public static Comment getUpdatedCommentForUpdate() {
        return Comment.builder()
                .id(COMMENT_ID_FOR_UPDATE)
                .time(UPDATED_DATE)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT_UPDATE)
                .build();
    }

    public static CommentResponse getCommentResponseForUpdate() {
        return CommentResponse.builder()
                .id(COMMENT_ID_FOR_UPDATE)
                .time(UPDATED_DATE)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT_UPDATE)
                .build();
    }

    //  full text search
    public static List<Comment> getListCommentsSearch() {
        return List.of(
                Comment.builder()
                        .id(1L)
                        .username(USERNAME_SEARCH)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT)
                        .time(CREATED_DATE)
                        .build(),
                Comment.builder()
                        .id(2L)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_SEARCH)
                        .time(CREATED_DATE)
                        .build()
        );
    }

    public static List<CommentResponse> getListCommentsResponseSearch() {
        return List.of(
                CommentResponse.builder()
                        .id(1L)
                        .username(USERNAME_SEARCH)
                        .newsId(NEWS_ID)
                        .text(TEXT_COMMENT)
                        .time(CREATED_DATE)
                        .build(),
                CommentResponse.builder()
                        .id(2L)
                        .username(USERNAME)
                        .newsId(NEWS_ID)
                        .text(TEXT_SEARCH)
                        .time(CREATED_DATE)
                        .build()
        );
    }

    //mapper
    public static CommentRequest getFillCommentRequest() {
        return CommentRequest.builder()
                .time(CREATED_DATE)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT)
                .build();
    }

    public static Comment getFillCommentWithId() {
        return Comment.builder()
                .id(COMMENT_ID_FOR_GET)
                .time(CREATED_DATE)
                .username(USERNAME)
                .newsId(NEWS_ID)
                .text(TEXT_COMMENT)
                .build();
    }

}
