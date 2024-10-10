package ru.clevertec.feignclient.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.feignclient.CommentsFeignClient;
import util.NewsTestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentsFeignServiceImplTest {

    @Mock
    private CommentsFeignClient commentsFeignClient;

    @InjectMocks
    private CommentsFeignServiceImpl commentsFeignService;

    @Test
    void shouldGetCommentsByNewsId() {
        //given
        long newsId = NewsTestData.NEWS_ID_FOR_GET;
        int pageNumberComment = NewsTestData.PAGE_NUMBER_COMMENT;
        List<CommentResponse> commentsResponse = NewsTestData.getListCommentsResponse();

        when(commentsFeignClient.getCommentsByNewsId(newsId, pageNumberComment))
                .thenReturn(commentsResponse);

        //when
        List<CommentResponse> actualValue = commentsFeignService.getCommentsByNewsId(newsId, pageNumberComment);

        //then
        assertEquals(commentsResponse.size(), actualValue.size());
    }

    @Test
    void shouldGetCommentById() {
        //given
        long commentId = NewsTestData.COMMENTS_ID_FOR_GET;
        CommentResponse commentResponse = NewsTestData.getCommentResponseForGetById();

        when(commentsFeignClient.getCommentById(commentId))
                .thenReturn(commentResponse);

        //when
        CommentResponse actualValue = commentsFeignService.getCommentById(commentId);

        //then
        assertNotNull(actualValue);
    }


}