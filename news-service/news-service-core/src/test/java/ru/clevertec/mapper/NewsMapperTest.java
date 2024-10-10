package ru.clevertec.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.dto.request.NewsRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.dto.response.NewsCommentsResponse;
import ru.clevertec.dto.response.NewsResponse;
import ru.clevertec.entity.News;
import util.NewsTestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class NewsMapperTest {

    @InjectMocks
    private NewsMapper newsMapper = new NewsMapperImpl();

    @Test
    void shouldConvertNewsRequestToNews() {
        //given
        NewsRequest newsRequest = NewsTestData.getFillNewsRequest();

        //when
        News actualNews = newsMapper.requestToNews(newsRequest);

        //then
        assertAll(
                () -> assertNull(actualNews.getId()),
                () -> assertEquals(newsRequest.time(), actualNews.getTime()),
                () -> assertEquals(newsRequest.title(), actualNews.getTitle()),
                () -> assertEquals(newsRequest.text(), actualNews.getText())
        );
    }

    @Test
    void shouldConvertNewsToNewsResponse() {
        //given
        News news = NewsTestData.getFillNewsWithId();

        //when
        NewsResponse actualNewsResponse = newsMapper.newsToResponse(news);

        //then
        assertAll(
                () -> assertNotNull(actualNewsResponse),
                () -> assertEquals(news.getId(), actualNewsResponse.id()),
                () -> assertEquals(news.getTime(), actualNewsResponse.time()),
                () -> assertEquals(news.getTitle(), actualNewsResponse.title()),
                () -> assertEquals(news.getText(), actualNewsResponse.text())
        );
    }

    @Test
    void shouldUpdateNewsFromNewsRequest() {
        //given
        NewsRequest newsRequest = NewsTestData.getFillNewsRequest();
        long newsId = NewsTestData.NEWS_ID_FOR_GET;


        //when
        News actualNews = newsMapper.updateFromRequest(newsId, newsRequest);

        //then
        assertAll(
                () -> assertNotNull(actualNews),
                () -> assertEquals(newsId, actualNews.getId()),
                () -> assertEquals(newsRequest.time(), actualNews.getTime()),
                () -> assertEquals(newsRequest.title(), actualNews.getTitle()),
                () -> assertEquals(newsRequest.text(), actualNews.getText())
        );
    }

    @Test
    void shouldConvertNewsToNewsResponseWithComments() {
        //given
        News news = NewsTestData.getFillNewsWithId();
        List<CommentResponse> commentResponses = NewsTestData.getCommentResponsesForMapper();

        //when
        NewsCommentsResponse actualNewsCommentsResponse = newsMapper.newsToCommentsResponse(news, commentResponses);

        //then
        assertAll(
                () -> assertNotNull(actualNewsCommentsResponse),
                () -> assertEquals(news.getId(), actualNewsCommentsResponse.id()),
                () -> assertEquals(news.getTime(), actualNewsCommentsResponse.time()),
                () -> assertEquals(news.getTitle(), actualNewsCommentsResponse.title()),
                () -> assertEquals(news.getText(), actualNewsCommentsResponse.text()),
                () -> assertNotNull(actualNewsCommentsResponse.comments()),
                () -> assertEquals(commentResponses.size(), actualNewsCommentsResponse.comments().size()),
                () -> assertEquals(commentResponses.getFirst().id(), actualNewsCommentsResponse.comments().getFirst().id()),
                () -> assertEquals(commentResponses.getFirst().time(), actualNewsCommentsResponse.comments().getFirst().time()),
                () -> assertEquals(commentResponses.getFirst().username(), actualNewsCommentsResponse.comments().getFirst().username()),
                () -> assertEquals(commentResponses.getFirst().newsId(), actualNewsCommentsResponse.comments().getFirst().newsId()),
                () -> assertEquals(commentResponses.getFirst().text(), actualNewsCommentsResponse.comments().getFirst().text())
        );
    }


}