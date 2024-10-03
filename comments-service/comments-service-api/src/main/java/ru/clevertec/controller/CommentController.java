package ru.clevertec.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.dto.request.CommentRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.entity.Comment;
import ru.clevertec.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //CRUD - create comment
    @PostMapping("/comments")
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse comment = commentService.createComment(commentRequest);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    //CRUD - get all comments
    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponse>> getAllComments(
            @RequestParam(
                    name = "pageNumber",
                    defaultValue = "1",
                    required = false) int pageNumber
    ) {
        List<CommentResponse> comments = commentService.getAllComments(pageNumber - 1);
        return new ResponseEntity<>(comments, comments.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    //CRUD - get comment by id
    @GetMapping(value = "/comments/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable("commentId") Long commentId) {
        CommentResponse comment = commentService.getCommentById(commentId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    //CRUD - update
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("commentId") Long commentId,
                                                         @Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse comment = commentService.updateComment(commentId, commentRequest);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    //CRUD - delete by id
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
    }

    //get all comments by newsId
    @GetMapping("/{newsId}/comments")
    public ResponseEntity<List<CommentResponse>> getAllCommentsByNewsId(
            @PathVariable("newsId") long newsId,
            @RequestParam(
                    name = "pageNumber",
                    defaultValue = "1",
                    required = false) int pageNumber
    ) {
        List<CommentResponse> comments = commentService.getAllCommentsByNewsId(newsId, pageNumber - 1);
        return new ResponseEntity<>(comments, comments.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    //delete all comments by newsId
    @DeleteMapping("{newsId}/comments")
    public void deleteCommentsByNewsId(@PathVariable("newsId") Long newsId) {
        commentService.deleteCommentsByNewsId(newsId);
    }

    @GetMapping("comments/search")
    public List<Comment> searchComments(
            @RequestParam(name = "text") String text,
            @RequestParam(
                    name = "limit",
                    defaultValue = "15",
                    required = false) int limit,
            @RequestParam(name = "fields") List<String> fields
    ) {
        return commentService.searchComments(text, fields, limit);
    }
}
