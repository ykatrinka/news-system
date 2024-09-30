package ru.clevertec.feignclient.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.feignclient.CommentsFeignClient;
import ru.clevertec.feignclient.CommentsFeignService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentsFeignServiceImpl implements CommentsFeignService {

    private final CommentsFeignClient commentsFeignClient;

    @Override
    public List<CommentResponse> getCommentsByNewsId(final Long newsId, final int pageNumber) {
        return commentsFeignClient.getCommentsByNewsId(newsId, pageNumber);
    }

    @Override
    public CommentResponse getCommentById(Long commentId) {
        return commentsFeignClient.getCommentById(commentId);
    }

    @Override
    public void deleteCommentsByNewsId(Long newsId) {
        commentsFeignClient.deleteCommentsByNewsId(newsId);
    }
}
