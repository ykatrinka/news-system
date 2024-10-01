package ru.clevertec.service.impl;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.dto.request.NewsRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.dto.response.NewsCommentsResponse;
import ru.clevertec.dto.response.NewsResponse;
import ru.clevertec.entity.News;
import ru.clevertec.exception.CommentNotFoundException;
import ru.clevertec.exception.NewsNotFoundException;
import ru.clevertec.exception.NotMatchNewsCommentException;
import ru.clevertec.feignclient.CommentsFeignService;
import ru.clevertec.mapper.NewsMapper;
import ru.clevertec.repository.NewsRepository;
import ru.clevertec.service.NewsService;
import ru.clevertec.util.Constants;
import ru.clevertec.util.ReflectionUtil;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final CommentsFeignService commentsFeignService;

    @Override
    public NewsResponse createNews(NewsRequest newsRequest) {
        News news = newsMapper.requestToNews(newsRequest);
        News savedNews = newsRepository.save(news);

        return newsMapper.newsToResponse(savedNews);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsResponse> getAllNews(int pageNumber) {
        PageRequest pageable = PageRequest.of(pageNumber, Constants.NEWS_PAGE_SIZE);
        Page<News> pageNews = newsRepository.findAll(pageable);

        return pageNews.getContent().stream()
                .map(newsMapper::newsToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public NewsResponse getNewsById(Long newsId) {
        return newsRepository.findById(newsId)
                .map(newsMapper::newsToResponse)
                .orElseThrow(
                        () -> NewsNotFoundException.getById(newsId)
                );
    }

    @Override
    public NewsResponse updateNews(Long newsId, NewsRequest newsRequest) {
        News news = newsRepository.findById(newsId)
                .map(updNews -> newsMapper.updateFromRequest(newsId, newsRequest))
                .orElseThrow(
                        () -> NewsNotFoundException.getById(newsId)
                );
        News updatedNews = newsRepository.save(news);

        return newsMapper.newsToResponse(updatedNews);
    }

    @Override
    public void deleteNews(Long newsId) {
        commentsFeignService.deleteCommentsByNewsId(newsId);
        newsRepository.findById(newsId).ifPresent(newsRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsCommentsResponse getNewsWithCommentsById(Long newsId, int pageNumber) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> NewsNotFoundException.getById(newsId));
        List<CommentResponse> comments = commentsFeignService.getCommentsByNewsId(newsId, pageNumber);

        return newsMapper.newsToCommentsResponse(news, comments);
    }

    @Transactional(readOnly = true)
    @Override
    public List<NewsCommentsResponse> getAllNewsWithComments(int pageNews, int pageComments) {
        PageRequest pageable = PageRequest.of(pageNews, Constants.NEWS_PAGE_SIZE);
        Page<News> newsPage = newsRepository.findAll(pageable);

        return newsPage.getContent().stream()
                .map(news -> newsMapper.newsToCommentsResponse(
                        news,
                        commentsFeignService.getCommentsByNewsId(news.getId(), pageComments + 1)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CommentResponse getNewsCommentById(Long newsId, Long commentId) {
        CommentResponse comment;
        try {
            comment = commentsFeignService.getCommentById(commentId);
        } catch (FeignException e) {
            throw CommentNotFoundException.getById(commentId);
        }

        if (!newsId.equals(comment.newsId())) {
            throw NotMatchNewsCommentException.getById(newsId, commentId);
        }

        return comment;
    }

    @Override
    public boolean isExistsNews(Long newsId) {
        return newsRepository.existsById(newsId);
    }

    @Override
    public List<News> searchNews(String text, List<String> fields, int limit) {

        List<String> searchableFields = ReflectionUtil.getFieldsByAnnotation(News.class, FullTextField.class);
        List<String> fieldsToSearchBy = fields.isEmpty() ? searchableFields : fields;

        boolean containsInvalidField = fieldsToSearchBy.stream().anyMatch(f -> !searchableFields.contains(f));

        if (containsInvalidField) {
            throw new IllegalArgumentException();
        }

        return newsRepository.searchBy(
                text, limit, fieldsToSearchBy.toArray(new String[0]));
    }
}
