package ru.clevertec.service.impl;


import feign.FeignException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import util.NewsTestData;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private NewsMapper newsMapper;

    @Mock
    private CommentsFeignService commentsFeignService;

    @InjectMocks
    private NewsServiceImpl newsService;


    @Test
    void shouldCreateNews() {
        //given
        NewsRequest newsRequest = NewsTestData.getNewsRequestForCreate();
        News news = NewsTestData.getNewsForCreate();
        News createdNews = NewsTestData.getFillNewsForCreate();
        NewsResponse newsResponse = NewsTestData.getNewsResponseForCreate();

        when(newsMapper.requestToNews(newsRequest))
                .thenReturn(news);
        when(newsRepository.save(news))
                .thenReturn(createdNews);
        when(newsMapper.newsToResponse(createdNews))
                .thenReturn(newsResponse);

        //when
        NewsResponse actualNewsResponse = newsService.createNews(newsRequest);

        //then
        assertAll(
                () -> assertNotNull(actualNewsResponse),
                () -> assertNotNull(actualNewsResponse.id()),
                () -> assertNotNull(actualNewsResponse.time()),
                () -> assertEquals(NewsTestData.NEWS_TITLE, actualNewsResponse.title()),
                () -> assertEquals(NewsTestData.NEWS_CONTENT, actualNewsResponse.text())
        );

        verify(newsMapper, times(1)).requestToNews(newsRequest);
        verify(newsRepository, times(1)).save(news);
        verify(newsMapper, times(1)).newsToResponse(createdNews);
    }

    @Test
    void shouldGetAllNews() {
        //given
        PageRequest pageable = PageRequest.of(
                NewsTestData.PAGE_NUMBER_NEWS,
                NewsTestData.PAGE_SIZE_NEWS
        );
        Page<News> pageNews = NewsTestData.getPageableListNews();
        List<NewsResponse> newsResponses = NewsTestData.getListNewsResponses();

        when(newsRepository.findAll(pageable))
                .thenReturn(pageNews);
        when(newsMapper.newsToResponse(pageNews.getContent().get(0)))
                .thenReturn(newsResponses.get(0));
        when(newsMapper.newsToResponse(pageNews.getContent().get(1)))
                .thenReturn(newsResponses.get(1));
        when(newsMapper.newsToResponse(pageNews.getContent().get(2)))
                .thenReturn(newsResponses.get(2));
        when(newsMapper.newsToResponse(pageNews.getContent().get(3)))
                .thenReturn(newsResponses.get(3));

        //when
        List<NewsResponse> actualNews = newsService.getAllNews(NewsTestData.PAGE_NUMBER_NEWS);

        //then
        assertEquals(newsResponses.size(), actualNews.size());

        verify(newsRepository, times(1)).findAll(pageable);
        verify(newsMapper, times(1)).newsToResponse(pageNews.getContent().get(0));
        verify(newsMapper, times(1)).newsToResponse(pageNews.getContent().get(1));
        verify(newsMapper, times(1)).newsToResponse(pageNews.getContent().get(2));
        verify(newsMapper, times(1)).newsToResponse(pageNews.getContent().get(3));
    }

    @Nested
    class getById {

        @Test
        void shouldGetNewsById() {
            //given
            Long newsId = NewsTestData.NEWS_ID_FOR_GET;
            News news = NewsTestData.getFillNewsForGetById();
            NewsResponse newsResponse = NewsTestData.getFillNewsResponseForGetById();

            when(newsRepository.findById(newsId)).thenReturn(Optional.of(news));
            when(newsMapper.newsToResponse(news)).thenReturn(newsResponse);

            //when
            NewsResponse actualNews = newsService.getNewsById(newsId);

            //then
            assertAll(
                    () -> assertNotNull(actualNews),
                    () -> assertEquals(newsId, actualNews.id()),
                    () -> assertEquals(NewsTestData.CREATED_DATE, actualNews.time()),
                    () -> assertEquals(NewsTestData.NEWS_TITLE, actualNews.title()),
                    () -> assertEquals(NewsTestData.NEWS_CONTENT, actualNews.text())
            );

            verify(newsRepository, times(1)).findById(newsId);
            verify(newsMapper, times(1)).newsToResponse(news);
        }

        @Test
        void shouldNotGetNewsById_whenNewsByIdNotFound() {
            //given
            Long newsId = NewsTestData.NEWS_ID_FOR_GET;
            when(newsRepository.findById(newsId))
                    .thenThrow(NewsNotFoundException.getById(newsId));

            //when, then
            assertThrows(NewsNotFoundException.class,
                    () -> newsService.getNewsById(newsId));

            verify(newsRepository, times(1))
                    .findById(newsId);
            verify(newsMapper, times(0))
                    .newsToResponse(any());
        }
    }

    @Nested
    class update {
        @Test
        void shouldUpdateNews() {
            //given
            long newsId = NewsTestData.NEWS_ID_FOR_UPDATE;
            NewsRequest newsRequest = NewsTestData.getNewsRequestForUpdate();
            Optional<News> news = NewsTestData.getNewsForUpdate();
            News updatedNews = NewsTestData.getUpdatedNewsForUpdate();
            NewsResponse newsResponse = NewsTestData.getNewsResponseForUpdate();

            when(newsRepository.findById(newsId))
                    .thenReturn(news);
            when(newsMapper.updateFromRequest(newsId, newsRequest))
                    .thenReturn(updatedNews);
            when(newsRepository.save(updatedNews)).thenReturn(updatedNews);
            when(newsMapper.newsToResponse(updatedNews)).thenReturn(newsResponse);

            //when
            NewsResponse actualNews = newsService.updateNews(newsId, newsRequest);

            //then
            assertAll(
                    () -> assertNotNull(actualNews),
                    () -> assertEquals(NewsTestData.NEWS_ID_FOR_UPDATE, actualNews.id()),
                    () -> assertEquals(NewsTestData.UPDATED_DATE, actualNews.time()),
                    () -> assertEquals(NewsTestData.NEWS_TITLE, actualNews.title()),
                    () -> assertEquals(NewsTestData.NEWS_CONTENT_UPDATE, actualNews.text())
            );


            verify(newsRepository, times(1))
                    .findById(newsId);
            verify(newsMapper, times(1))
                    .updateFromRequest(newsId, newsRequest);
            verify(newsRepository, times(1))
                    .save(updatedNews);
            verify(newsMapper, times(1))
                    .newsToResponse(updatedNews);
        }

        @Test
        void shouldNotUpdateNews_whenNewsNotFound() {
            //given
            long newsId = NewsTestData.NEWS_ID_FOR_UPDATE;
            NewsRequest newsRequest = NewsTestData.getNewsRequestForUpdate();

            when(newsRepository.findById(newsId))
                    .thenReturn(Optional.empty());


            //when, then
            assertThrows(NewsNotFoundException.class,
                    () -> newsService.updateNews(newsId, newsRequest));

            verify(newsRepository, times(1))
                    .findById(newsId);
        }

    }

    @Test
    void shouldDeleteNews() {
        //given
        long newsId = NewsTestData.NEWS_ID_FOR_DELETE;
        News news = NewsTestData.getFillNewsForGetById();

        //when
        when(newsRepository.findById(newsId))
                .thenReturn(Optional.of(news));

        //then
        newsService.deleteNews(newsId);
        verify(newsRepository, times(1)).findById(newsId);
        verify(newsRepository, times(1)).delete(news);
    }


    @Nested
    class getWithCommentsById {

        @Test
        void shouldGetNewsByIdWithComments() {
            //given
            int pageNumber = NewsTestData.PAGE_NUMBER_COMMENT;
            Long newsId = NewsTestData.NEWS_ID_FOR_GET;
            News news = NewsTestData.getFillNewsForGetById();
            List<CommentResponse> commentsResponse = NewsTestData.getListCommentsResponse();
            NewsCommentsResponse newsResponse = NewsTestData.getFillNewsResponseWithComments();

            when(newsRepository.findById(newsId)).thenReturn(Optional.of(news));
            when(commentsFeignService.getCommentsByNewsId(newsId, pageNumber))
                    .thenReturn(commentsResponse);
            when(newsMapper.newsToCommentsResponse(news, commentsResponse))
                    .thenReturn(newsResponse);

            //when
            NewsCommentsResponse actualNews = newsService.getNewsByIdWithComments(newsId, pageNumber);

            //then
            assertAll(
                    () -> assertNotNull(actualNews),
                    () -> assertEquals(newsId, actualNews.id()),
                    () -> assertEquals(NewsTestData.CREATED_DATE, actualNews.time()),
                    () -> assertEquals(NewsTestData.NEWS_TITLE, actualNews.title()),
                    () -> assertEquals(NewsTestData.NEWS_CONTENT, actualNews.text()),
                    () -> assertEquals(commentsResponse.size(), actualNews.comments().size())
            );

            verify(newsRepository, times(1))
                    .findById(newsId);
            verify(commentsFeignService, times(1))
                    .getCommentsByNewsId(newsId, pageNumber);
            verify(newsMapper, times(1))
                    .newsToCommentsResponse(news, commentsResponse);
        }

        @Test
        void shouldNotGetNewsWithCommentById_whenNewsNotFound() {
            //given
            int pageNumber = NewsTestData.PAGE_NUMBER_COMMENT;
            Long newsId = NewsTestData.NEWS_ID_FOR_GET;

            when(newsRepository.findById(newsId))
                    .thenThrow(NewsNotFoundException.getById(newsId));

            //when, then
            assertThrows(NewsNotFoundException.class,
                    () -> newsService.getNewsByIdWithComments(newsId, pageNumber));

            verify(newsRepository, times(1))
                    .findById(newsId);
            verify(commentsFeignService, times(0))
                    .getCommentsByNewsId(newsId, pageNumber);
            verify(newsMapper, times(0))
                    .newsToResponse(any());
        }
    }

    @Test
    void shouldGetAllNewsWithComments() {
        //given
        PageRequest pageable = PageRequest.of(
                NewsTestData.PAGE_NUMBER_NEWS,
                NewsTestData.PAGE_SIZE_NEWS
        );
        Page<News> pageNews = NewsTestData.getListNewsForGetAllWithComments();
        List<CommentResponse> commentsResponse = NewsTestData.getListCommentsResponse();
        List<NewsCommentsResponse> newsWithComments = NewsTestData.getFillListNewsResponseWithComments();

        when(newsRepository.findAll(pageable))
                .thenReturn(pageNews);
        when(commentsFeignService.getCommentsByNewsId(
                NewsTestData.NEWS_ID_FOR_COMMENTS,
                NewsTestData.PAGE_NUMBER_COMMENT))
                .thenReturn(commentsResponse);
        when(newsMapper.newsToCommentsResponse(pageNews.getContent().getFirst(), commentsResponse))
                .thenReturn(newsWithComments.getFirst());

        //when
        List<NewsCommentsResponse> actualNews = newsService.getAllNewsWithComments(
                NewsTestData.PAGE_NUMBER_NEWS,
                NewsTestData.PAGE_NUMBER_COMMENT - 1
        );

        //then
        assertEquals(newsWithComments.size(), actualNews.size());
        assertEquals(newsWithComments.getFirst().comments().size(), actualNews.getFirst().comments().size());

        verify(newsRepository, times(1)).findAll(pageable);
        verify(commentsFeignService, times(1))
                .getCommentsByNewsId(
                        NewsTestData.NEWS_ID_FOR_COMMENTS,
                        NewsTestData.PAGE_NUMBER_COMMENT
                );
        verify(newsMapper, times(1))
                .newsToCommentsResponse(pageNews.getContent().getFirst(), commentsResponse);
    }


    @Nested
    class getCommentById {

        @Test
        void shouldGetCommentById() {
            //given
            Long newsId = NewsTestData.NEWS_ID_FOR_COMMENTS;
            Long commentId = NewsTestData.COMMENTS_ID_FOR_GET;
            CommentResponse comment = NewsTestData.getCommentResponseForGetById();

            when(commentsFeignService.getCommentById(commentId))
                    .thenReturn(comment);

            //when
            CommentResponse actualComment = newsService.getNewsCommentById(newsId, commentId);

            //then
            assertAll(
                    () -> assertNotNull(actualComment),
                    () -> assertEquals(commentId, actualComment.id()),
                    () -> assertEquals(comment.time(), actualComment.time()),
                    () -> assertEquals(comment.username(), actualComment.username()),
                    () -> assertEquals(comment.newsId(), actualComment.newsId()),
                    () -> assertEquals(comment.text(), actualComment.text())
            );

            verify(commentsFeignService, times(1)).getCommentById(commentId);
        }

        @Test
        void shouldNotGetCommentById_whenCommentByIdNotFound() {
            //given
            Long newsId = NewsTestData.NEWS_ID_FOR_COMMENTS;
            Long commentId = NewsTestData.COMMENTS_ID_FOR_GET;

            when(commentsFeignService.getCommentById(commentId))
                    .thenThrow(FeignException.class);

            //when, then
            assertThrows(CommentNotFoundException.class,
                    () -> newsService.getNewsCommentById(newsId, commentId));

            verify(commentsFeignService, times(1)).getCommentById(commentId);
        }

        @Test
        void shouldNotGetCommentById_whenCommentByNoMachWithNewsId() {
            //given
            Long newsId = NewsTestData.NEWS_ID_FOR_COMMENTS;
            Long commentId = NewsTestData.COMMENTS_ID_FOR_GET;
            CommentResponse comment = NewsTestData.getCommentResponseWithOtherNewsId();

            when(commentsFeignService.getCommentById(commentId))
                    .thenReturn(comment);

            //when, then
            assertThrows(NotMatchNewsCommentException.class,
                    () -> newsService.getNewsCommentById(newsId, commentId));

            verify(commentsFeignService, times(1)).getCommentById(commentId);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldReturnTrueIfNewsIsExists(boolean newsExists) {
        //given
        Long newsId = NewsTestData.NEWS_ID_FOR_GET;
        when(newsRepository.existsById(newsId)).thenReturn(newsExists);

        //when
        boolean actualValue = newsService.isExistsNews(newsId);

        //then
        assertEquals(newsExists, actualValue);
        verify(newsRepository, times(1)).existsById(newsId);
    }


    @Nested
    class search {

        @Test
        void shouldGetNewsWithFullTextSearch() {
            //given
            List<News> news = NewsTestData.getListNewsSearch();
            List<NewsResponse> newsResponses = NewsTestData.getListNewsResponseSearch();

            when(newsRepository.searchBy(NewsTestData.SEARCH_VALUE, 1,
                    NewsTestData.SEARCH_FIELDS_ARRAY))
                    .thenReturn(news);
            when(newsMapper.newsToResponse(news.get(0)))
                    .thenReturn(newsResponses.get(0));
            when(newsMapper.newsToResponse(news.get(1)))
                    .thenReturn(newsResponses.get(1));

            //when
            List<NewsResponse> actualNews = newsService.searchNews(
                    NewsTestData.SEARCH_VALUE,
                    NewsTestData.SEARCH_FIELDS,
                    1
            );

            //then
            assertEquals(newsResponses.size(), actualNews.size());
            verify(newsRepository, times(1))
                    .searchBy(NewsTestData.SEARCH_VALUE, 1,
                            NewsTestData.SEARCH_FIELDS_ARRAY);
            verify(newsMapper, times(2)).newsToResponse(any());
        }

        @Test
        void shouldNotGetCommentsWithFullTextSearch_whenFieldsIsNotValid() {
            // given, when, then
            assertThrows(
                    IllegalArgumentException.class,
                    () -> newsService.searchNews(
                            NewsTestData.SEARCH_VALUE,
                            NewsTestData.SEARCH_NOT_VALID_FIELDS, 1));
        }

    }

}