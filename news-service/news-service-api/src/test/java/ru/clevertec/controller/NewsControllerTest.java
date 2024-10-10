package ru.clevertec.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.dto.request.NewsRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.dto.response.NewsCommentsResponse;
import ru.clevertec.dto.response.NewsResponse;
import ru.clevertec.exception.CommentNotFoundException;
import ru.clevertec.exception.NewsNotFoundException;
import ru.clevertec.exception.NotMatchNewsCommentException;
import ru.clevertec.service.NewsService;
import util.NewsTestData;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataJpa
@WebMvcTest(NewsController.class)
@AutoConfigureMockMvc
class NewsControllerTest {

    @MockBean
    private NewsService newsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateNews() throws Exception {
        //given
        NewsRequest newsRequest = NewsTestData.getNewsRequest();
        NewsResponse newsResponse = NewsTestData.getNewsResponse();
        String newsJson = objectMapper.writeValueAsString(newsRequest);

        when(newsService.createNews(newsRequest))
                .thenReturn(newsResponse);

        //when, then
        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newsJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(newsResponse.id()))
                .andExpect(jsonPath("$.title").value(newsResponse.title()))
                .andExpect(jsonPath("$.text").value(newsResponse.text())
                );

        verify(newsService, times(1)).createNews(newsRequest);

    }

    @Test
    void shouldGetAllNews() throws Exception {
        //given
        List<NewsResponse> news = NewsTestData.getListWithTwoCommentResponse();
        when(newsService.getAllNews(0))
                .thenReturn(news);

        //when, then
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(newsService, times(0))
                .getAllNews(1);
    }

    @Nested
    class GetById {

        @Test
        void shouldGetNewsById() throws Exception {
            //given
            Long newsId = NewsTestData.NEWS_ID;
            NewsResponse newsResponse = NewsTestData.getNewsResponse();

            when(newsService.getNewsById(newsId))
                    .thenReturn(newsResponse);

            //when, then
            mockMvc.perform(get("/news/{newsId}", newsId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value(IsNull.notNullValue()))
                    .andExpect(jsonPath("$.id").value(newsResponse.id()))
                    .andExpect(jsonPath("$.title").value(newsResponse.title()))
                    .andExpect(jsonPath("$.text").value(newsResponse.text())
                    );

            verify(newsService, times(1))
                    .getNewsById(newsId);
        }


        @Test
        void shouldNotGetNewsById_whenNewsNotFound() throws Exception {
            //given
            Long newsId = NewsTestData.NEWS_ID;

            when(newsService.getNewsById(newsId))
                    .thenThrow(NewsNotFoundException.class);

            //when, then
            mockMvc.perform(get("/news/{newsId}", newsId))
                    .andExpect(status().isNotFound());

            verify(newsService, times(1))
                    .getNewsById(newsId);
        }
    }

    @Test
    void shouldUpdateNewsById() throws Exception {
        //given
        Long newsId = NewsTestData.NEWS_ID;
        NewsRequest newsRequest = NewsTestData.getNewsRequest();
        NewsResponse updatedNews = NewsTestData.getNewsResponse();
        String newsJson = objectMapper.writeValueAsString(newsRequest);

        when(newsService.updateNews(newsId, newsRequest))
                .thenReturn(updatedNews);

        //when, then
        mockMvc.perform(put("/news/{newsId}", newsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newsJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedNews.id()))
                .andExpect(jsonPath("$.title").value(updatedNews.title()))
                .andExpect(jsonPath("$.text").value(updatedNews.text())
                )
        ;

        verify(newsService, times(1))
                .updateNews(newsId, newsRequest);

    }

    @Test
    void shouldGetAllNewsWithComments() throws Exception {
        //given
        List<NewsCommentsResponse> news = NewsTestData.getFillListNewsResponseWithComments();

        when(newsService.getAllNewsWithComments(0, 0))
                .thenReturn(news);

        //when, then
        mockMvc.perform(get("/news/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(newsService, times(0))
                .getAllNews(1);
    }

    @Nested
    class newsByIdWithComments {

        @Test
        void shouldGetNewsByIdWithComments() throws Exception {
            //given
            Long newsId = NewsTestData.NEWS_ID;
            NewsCommentsResponse newsWithComments = NewsTestData.getFillNewsResponseWithComments();

            when(newsService.getNewsByIdWithComments(newsId, 0))
                    .thenReturn(newsWithComments);

            //when, then
            mockMvc.perform(get("/news/{newsId}/comments", newsId))
                    .andExpect(status().isOk());

            verify(newsService, times(0))
                    .getNewsByIdWithComments(newsId, 0);
        }

        @Test
        void shouldNotGetNewsByIdWithComments_whenNewsIdNotFound() throws Exception {
            //given
            Long newsId = NewsTestData.NEWS_ID;

            when(newsService.getNewsByIdWithComments(newsId, 1))
                    .thenThrow(NewsNotFoundException.class);

            //when, then
            mockMvc.perform(get("/news/{newsId}/comments", newsId))
                    .andExpect(status().isNotFound());

            verify(newsService, times(1))
                    .getNewsByIdWithComments(newsId, 1);
        }

    }

    @Nested
    class commentById {

        @Test
        void shouldGetCommentById() throws Exception {
            //given
            Long newsId = NewsTestData.NEWS_ID;
            Long commentId = NewsTestData.COMMENT_ID;
            CommentResponse commentResponse = NewsTestData.getCommentResponse();

            when(newsService.getNewsCommentById(newsId, commentId))
                    .thenReturn(commentResponse);

            //when, then
            mockMvc.perform(get("/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(commentResponse.id()))
                    .andExpect(jsonPath("$.newsId").value(commentResponse.newsId()))
                    .andExpect(jsonPath("$.username").value(commentResponse.username()))
                    .andExpect(jsonPath("$.text").value(commentResponse.text()))
            ;

            verify(newsService, times(1))
                    .getNewsCommentById(newsId, commentId);
        }

        @Test
        void shouldNotGetCommentById_whenNewsIdNotFound() throws Exception {
            //given
            Long newsId = NewsTestData.NEWS_ID;
            Long commentId = NewsTestData.COMMENT_ID;

            when(newsService.getNewsCommentById(newsId, commentId))
                    .thenThrow(NewsNotFoundException.class);

            //when, then
            mockMvc.perform(get("/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isNotFound());

            verify(newsService, times(1))
                    .getNewsCommentById(newsId, commentId);
        }

        @Test
        void shouldNotGetCommentById_whenNewsIdNotMatchCommentId() throws Exception {
            //given
            Long newsId = NewsTestData.NEWS_ID;
            Long commentId = NewsTestData.COMMENT_ID;

            when(newsService.getNewsCommentById(newsId, commentId))
                    .thenThrow(NotMatchNewsCommentException.class);

            //when, then
            mockMvc.perform(get("/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isBadRequest());

            verify(newsService, times(1))
                    .getNewsCommentById(newsId, commentId);
        }

        @Test
        void shouldNotGetCommentById_whenCommentIdNotFound() throws Exception {
            //given
            Long newsId = NewsTestData.NEWS_ID;
            Long commentId = NewsTestData.COMMENT_ID;

            when(newsService.getNewsCommentById(newsId, commentId))
                    .thenThrow(CommentNotFoundException.class);

            //when, then
            mockMvc.perform(get("/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isNotFound());

            verify(newsService, times(1))
                    .getNewsCommentById(newsId, commentId);
        }

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldReturnTrueIfNewsIsExists(boolean newsExists) throws Exception {
        //given
        Long newsId = NewsTestData.NEWS_ID;
        when(newsService.isExistsNews(newsId)).thenReturn(newsExists);

        //when
        mockMvc.perform(get("/news/exists/{newsId}", newsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(newsExists));

        //then
        verify(newsService, times(1))
                .isExistsNews(newsId);

    }


    @Nested
    class Search {

        @Test
        void shouldGetNewsWithFullTextSearch() throws Exception {
            //given
            String searchValue = NewsTestData.SEARCH_VALUE;
            List<String> searchFields = NewsTestData.SEARCH_FIELDS;
            int searchLimit = NewsTestData.SEARCH_LIMIT;
            List<NewsResponse> newsResponses = NewsTestData.getListNewsResponse();

            when(newsService.searchNews(searchValue, searchFields, searchLimit))
                    .thenReturn(newsResponses);

            //when
            mockMvc.perform(get("/news/search")
                            .param("text", searchValue)
                            .param("fields", NewsTestData.SEARCH_FIELDS_ARRAY)
                            .param("limit", String.valueOf(searchLimit)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));

            //then
            verify(newsService, times(1))
                    .searchNews(searchValue, searchFields, searchLimit);

        }

        @Test
        void shouldNotGetNewsWithFullTextSearch_whenSearchFieldsIsNotValid() throws Exception {
            //given
            String searchValue = NewsTestData.SEARCH_VALUE;
            List<String> searchFields = NewsTestData.SEARCH_NOT_VALID_FIELDS;
            int searchLimit = NewsTestData.SEARCH_LIMIT;

            when(newsService.searchNews(searchValue, searchFields, searchLimit))
                    .thenThrow(IllegalArgumentException.class);

            //when
            mockMvc.perform(get("/news/search")
                            .param("text", searchValue)
                            .param("fields", NewsTestData.SEARCH_NOT_VALID_FIELDS_ARRAY)
                            .param("limit", String.valueOf(searchLimit))
                    )
                    .andExpect(status().isInternalServerError());

            //then
            verify(newsService, times(1))
                    .searchNews(searchValue, searchFields, searchLimit);

        }
    }

}