package ru.clevertec.service;

import ru.clevertec.dto.request.NewsRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.dto.response.NewsCommentsResponse;
import ru.clevertec.dto.response.NewsResponse;
import ru.clevertec.entity.News;

import java.util.List;

public interface NewsService {

    NewsResponse createNews(NewsRequest newsRequest);

    List<NewsResponse> getAllNews(int pageNumber);

    NewsResponse getNewsById(Long newsId);

    NewsCommentsResponse getNewsWithCommentsById(Long newsId, int pageNumber);

    NewsResponse updateNews(Long newsId, NewsRequest newsRequest);

    void deleteNews(Long newsId);

    List<NewsCommentsResponse> getAllNewsWithComments(int pageNews, int pageComments);

    CommentResponse getNewsCommentById(Long newsId, Long commentId);

    boolean isExistsNews(Long newsId);

    List<News> searchNews(String text, List<String> fields, int limit);
}
