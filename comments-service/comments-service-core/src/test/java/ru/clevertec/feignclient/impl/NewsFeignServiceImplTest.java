package ru.clevertec.feignclient.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.clevertec.feignclient.NewsFeignClient;
import util.CommentTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsFeignServiceImplTest {

    @Mock
    private NewsFeignClient newsFeignClient;

    @InjectMocks
    private NewsFeignServiceImpl newsFeignService;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldReturnResultNewsIsNotExists(boolean exists) {
        //given
        long newsId = CommentTestData.NEWS_ID;
        when(newsFeignClient.isExistsNews(newsId))
                .thenReturn(new ResponseEntity<>(exists, HttpStatus.OK));

        //when
        boolean actualValue = newsFeignService.isNotExistsNews(newsId);

        //then
        assertEquals(actualValue, !exists);

    }

}