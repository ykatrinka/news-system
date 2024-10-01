package ru.clevertec.service;

import ru.clevertec.dto.request.CommentRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(CommentRequest commentRequest);

    List<CommentResponse> getAllComments(int pageNumber);

    List<CommentResponse> getAllCommentsByNewsId(long newsId, int pageNumber);

    CommentResponse getCommentById(Long commentsId);

    CommentResponse updateComment(Long commentId, CommentRequest newsRequest);

    void deleteComment(Long commentId);

    void deleteCommentsByNewsId(Long newsId);

    List<Comment> searchComments(String text, List<String> fields, int limit);
}
