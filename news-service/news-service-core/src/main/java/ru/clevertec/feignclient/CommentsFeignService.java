package ru.clevertec.feignclient;

import ru.clevertec.dto.response.CommentResponse;

import java.util.List;

public interface CommentsFeignService {
    List<CommentResponse> getCommentsByNewsId(Long newsId, int pageNumber);

    CommentResponse getCommentById(Long commentId);

    void deleteCommentsByNewsId(Long newsId);
}
