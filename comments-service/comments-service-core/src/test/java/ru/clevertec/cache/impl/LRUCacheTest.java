package ru.clevertec.cache.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.entity.Comment;
import util.CommentTestData;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LRUCacheTest {

    public static final long COMMENT_ID = 1L;
    public static final long COMMENT_ID_SECOND = 2L;
    public static final long COMMENT_ID_THIRD = 3L;
    public static final String CHANGED_COMMENT = "Changed comment";
    private LRUCache<Long, Comment> cache;

    @BeforeEach
    void setUp() {
        cache = new LRUCache<>();
    }

    @Test
    void shouldPutCommentInEmptyCache() {
        Comment comment = CommentTestData.getCommentForCreate();
        comment.setId(COMMENT_ID);
        cache.put(comment.getId(), comment);

        assertNotNull(cache);
        assertTrue(cache.get(comment.getId()).isPresent());
    }

    @Test
    void shouldPutCommentInNotEmptyCache() {
        Comment comment1 = Comment.builder()
                .id(COMMENT_ID)
                .build();
        Comment comment2 = Comment.builder()
                .id(COMMENT_ID_SECOND)
                .build();

        cache.put(comment1.getId(), comment1);
        cache.put(comment2.getId(), comment2);

        assertNotNull(cache);
        assertTrue(cache.get(comment2.getId()).isPresent());
    }

    @Test
    void shouldPutCommentInCache_whenCommentIsExists() {
        Comment comment1 = Comment.builder()
                .id(COMMENT_ID)
                .build();
        Comment comment2 = Comment.builder()
                .id(COMMENT_ID_SECOND)
                .build();

        cache.put(comment1.getId(), comment1);
        cache.put(comment2.getId(), comment2);

        comment2.setText(CHANGED_COMMENT);
        cache.put(comment2.getId(), comment2);


        assertNotNull(cache);
        assertTrue(cache.get(comment2.getId()).isPresent());
        assertEquals(CHANGED_COMMENT, cache.get(comment2.getId()).get().getText());
    }

    @Test
    void shouldPutCommentInNotEmptyCache_whenCapacityIsMax() {
        fillCache();
        Comment comment = Comment.builder()
                .id(11L)
                .build();

        cache.put(comment.getId(), comment);

        assertNotNull(cache);
        assertTrue(cache.get(comment.getId()).isPresent());
        assertTrue(cache.get(COMMENT_ID).isEmpty());
    }

    private void fillCache() {
        Comment comment;
        for (int i = 0; i < 10; i++) {
            comment = Comment.builder()
                    .id((long) (i + 1))
                    .build();
            cache.put(comment.getId(), comment);
        }

    }

    @Test
    void shouldGetCommentInCache() {
        Comment comment = CommentTestData.getCommentForCreate();
        comment.setId(COMMENT_ID);
        cache.put(comment.getId(), comment);

        Optional<Comment> actualComment = cache.get(comment.getId());

        assertTrue(actualComment.isPresent());
    }

    @Test
    void shouldNotGetCommentInCache_whenCommentNotFound() {
        Optional<Comment> actualComment = cache.get(COMMENT_ID);
        assertTrue(actualComment.isEmpty());
    }

    @Test
    void shouldDeleteCommentInCache() {
        Comment comment = CommentTestData.getCommentForCreate();
        comment.setId(COMMENT_ID);
        cache.put(comment.getId(), comment);
        cache.delete(COMMENT_ID);
        Optional<Comment> actualComment = cache.get(COMMENT_ID);
        assertTrue(actualComment.isEmpty());
    }

    @Test
    void shouldNotDeleteCommentInCache_whenCommentNotFound() {
        cache.delete(COMMENT_ID);
        Optional<Comment> actualComment = cache.get(COMMENT_ID);
        assertTrue(actualComment.isEmpty());
    }

    @Test
    void shouldDeleteCommentInCacheWithMoreThenOneComment() {
        Comment comment = CommentTestData.getCommentForCreate();
        comment.setId(COMMENT_ID);
        cache.put(comment.getId(), comment);

        Comment comment2 = CommentTestData.getCommentForCreate();
        comment2.setId(COMMENT_ID_SECOND);
        cache.put(comment2.getId(), comment2);

        Comment comment3 = CommentTestData.getCommentForCreate();
        comment3.setId(COMMENT_ID_THIRD);
        cache.put(comment3.getId(), comment3);

        cache.delete(COMMENT_ID_SECOND);
        Optional<Comment> actualComment = cache.get(COMMENT_ID_SECOND);
        assertTrue(actualComment.isEmpty());
    }

}