package ru.clevertec.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.dto.request.CommentRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.entity.Comment;
import ru.clevertec.exception.CommentNotFoundException;
import ru.clevertec.exception.NewsNotExistsException;
import ru.clevertec.feignclient.NewsFeignService;
import ru.clevertec.mapper.CommentMapper;
import ru.clevertec.repository.CommentRepository;
import ru.clevertec.service.CommentService;
import ru.clevertec.util.Constants;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final NewsFeignService newsFeignService;

    private void checkNewsId(CommentRequest commentRequest) {
        if (newsFeignService.isNotExistsNews(commentRequest.newsId())) {
            throw NewsNotExistsException.existsById(commentRequest.newsId());
        }
    }

    @Override
    public CommentResponse createComment(CommentRequest commentRequest) {
        checkNewsId(commentRequest);
        Comment comment = commentMapper.requestToComment(commentRequest);
        Comment savedComment = commentRepository.save(comment);

        return commentMapper.commentToResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getAllComments(int pageNumber) {
        PageRequest pageable = PageRequest.of(pageNumber, Constants.COMMENTS_PAGE_SIZE);
        Page<Comment> pageComments = commentRepository.findAll(pageable);

        return pageComments.getContent().stream()
                .map(commentMapper::commentToResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long commentsId) {
        return commentRepository.findById(commentsId)
                .map(commentMapper::commentToResponse)
                .orElseThrow(
                        () -> CommentNotFoundException.getById(commentsId)
                );
    }

    @Override
    public CommentResponse updateComment(Long commentId, CommentRequest commentRequest) {
        checkNewsId(commentRequest);
        Comment comment = commentRepository.findById(commentId)
                .map(updComment -> commentMapper.updateFromRequest(commentId, commentRequest))
                .orElseThrow(
                        () -> CommentNotFoundException.getById(commentId)
                );
        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.commentToResponse(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.findById(commentId).ifPresent(commentRepository::delete);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponse> getAllCommentsByNewsId(long newsId, int pageNumber) {
        if (newsFeignService.isNotExistsNews(newsId)) {
            throw NewsNotExistsException.existsById(newsId);
        }

        PageRequest pageable = PageRequest.of(pageNumber, Constants.COMMENTS_PAGE_SIZE);
        Page<Comment> pageComments = commentRepository.findByNewsId(newsId, pageable);

        return pageComments.getContent().stream()
                .map(commentMapper::commentToResponse)
                .toList();
    }

    @Override
    public void deleteCommentsByNewsId(Long newsId) {
        commentRepository.deleteAllByNewsId(newsId);
    }
}
