package ru.clevertec.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.dto.request.NewsRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.dto.response.NewsCommentsResponse;
import ru.clevertec.dto.response.NewsResponse;
import ru.clevertec.entity.News;
import ru.clevertec.service.NewsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    //CRUD - create
    @PostMapping
    public ResponseEntity<NewsResponse> createNews(@Valid @RequestBody NewsRequest newsRequest) {
        NewsResponse news = newsService.createNews(newsRequest);
        return new ResponseEntity<>(news, HttpStatus.CREATED);
    }

    //CRUD - get all without comments
    @GetMapping
    public ResponseEntity<List<NewsResponse>> getAllNews(
            @RequestParam(
                    name = "pageNumber",
                    defaultValue = "1",
                    required = false) int pageNumber) {
        List<NewsResponse> news = newsService.getAllNews(pageNumber - 1);
        return new ResponseEntity<>(news, news.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    //CRUD - get news by id without comments
    @GetMapping(value = "/{newsId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable("newsId") Long newsId) {
        NewsResponse news = newsService.getNewsById(newsId);
        return new ResponseEntity<>(news, HttpStatus.OK);
    }

    //CRUD - update
    @PostMapping("/{newsId}")
    public ResponseEntity<NewsResponse> updateNews(@PathVariable("newsId") Long newsId,
                                                   @Valid @RequestBody NewsRequest newsRequest) {
        NewsResponse news = newsService.updateNews(newsId, newsRequest);
        return new ResponseEntity<>(news, HttpStatus.OK);
    }

    //CRUD - delete by id
    @DeleteMapping("/{newsId}")
    public void deleteNews(@PathVariable("newsId") Long newsId) {
        newsService.deleteNews(newsId);
    }

    //get all news with comments
    @GetMapping(value = "/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NewsCommentsResponse>> getAllNewsWithComments(
            @RequestParam(
                    name = "pageNews",
                    defaultValue = "1",
                    required = false) int pageNews,
            @RequestParam(
                    name = "pageComments",
                    defaultValue = "1",
                    required = false) int pageComments) {
        List<NewsCommentsResponse> news = newsService.getAllNewsWithComments(pageNews - 1, pageComments - 1);
        return new ResponseEntity<>(news, news.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    // get news by id with comments
    @GetMapping(value = "/{newsId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NewsCommentsResponse> getNewsById(@PathVariable("newsId") Long newsId,
                                                            @RequestParam(
                                                                    name = "pageNumber",
                                                                    defaultValue = "1",
                                                                    required = false) int pageNumber) {

        NewsCommentsResponse news = newsService.getNewsWithCommentsById(newsId, pageNumber);
        return new ResponseEntity<>(news, HttpStatus.OK);
    }

    // get comment by id
    @GetMapping(value = "/{newsId}/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable("newsId") Long newsId,
                                                          @PathVariable("commentId") Long commentId) {
        CommentResponse news = newsService.getNewsCommentById(newsId, commentId);
        return new ResponseEntity<>(news, HttpStatus.OK);
    }


    @GetMapping("/exists/{newsId}")
    public Boolean isExistsNews(@PathVariable("newsId") Long newsId) {
        return newsService.isExistsNews(newsId);
    }

    @GetMapping("/search")
    public List<News> searchNews(
            @RequestParam(name = "text") String text,
            @RequestParam(
                    name = "limit",
                    defaultValue = "15",
                    required = false) int limit,
            @RequestParam(name = "fields") List<String> fields
    ) {
        return newsService.searchNews(text, fields, limit);
    }
}
