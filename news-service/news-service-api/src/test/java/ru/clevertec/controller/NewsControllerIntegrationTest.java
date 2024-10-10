package ru.clevertec.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.clevertec.dto.request.NewsRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.dto.response.NewsCommentsResponse;
import ru.clevertec.dto.response.NewsResponse;
import util.NewsTestData;

import java.net.URI;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Sql(
        scripts = "classpath:testdata/add-test-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@ActiveProfiles("test")
@Testcontainers
@WireMockTest(httpPort = 8082)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NewsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static final PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void shouldCreateNewsAndReturnStatus201() throws Exception {
        //given
        NewsRequest newsRequest = NewsTestData.getNewsRequest();
        HttpEntity<NewsRequest> httpRequest = new HttpEntity<>(newsRequest);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news",
                HttpMethod.POST,
                httpRequest,
                String.class);


        //when
        NewsResponse actualResponse = objectMapper.readValue(
                responseEntity.getBody(),
                new TypeReference<>() {
                });

        //then
        assertAll(
                () -> assertNotNull(actualResponse.id()),
                () -> assertEquals(NewsTestData.NEWS_ID_NEW, actualResponse.id()),
                () -> assertEquals(newsRequest.title(), actualResponse.title()),
                () -> assertEquals(newsRequest.text(), actualResponse.text()),
                () -> assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }

    @Test
    void shouldGetAllNews() throws JsonProcessingException {
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news",
                HttpMethod.GET,
                null,
                String.class);


        //when
        List<NewsResponse> actualResponse = objectMapper.readValue(
                responseEntity.getBody(),
                new TypeReference<>() {
                });

        //then
        assertAll(
                () -> assertNotNull(actualResponse),
                () -> assertEquals(3, actualResponse.size()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }


    @Test
    void shouldGetNewsById() throws Exception {
        //given
        long newsId = NewsTestData.NEWS_ID;

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/" + newsId,
                HttpMethod.GET,
                null,
                String.class);


        NewsResponse actualResponse = objectMapper.readValue(
                responseEntity.getBody(),
                new TypeReference<>() {
                });

        //then
        assertAll(
                () -> assertNotNull(actualResponse),
                () -> assertEquals(NewsTestData.NEWS_TITLE, actualResponse.title()),
                () -> assertEquals(NewsTestData.NEWS_TEXT, actualResponse.text()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );
    }

    @Test
    void shouldNotGetNewsById_whenNewsNotFound() {
        //given
        long newsId = NewsTestData.NEWS_ID_NOT_FOUND;

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/" + newsId,
                HttpMethod.GET,
                null,
                String.class);


        //then
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );
    }

    @Test
    void shouldUpdateNewsById() throws Exception {
        //given
        long newsId = NewsTestData.NEWS_ID_UPD;
        NewsRequest newsRequest = NewsTestData.getNewsRequest();
        HttpEntity<NewsRequest> httpRequest = new HttpEntity<>(newsRequest);

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/" + newsId,
                HttpMethod.PUT,
                httpRequest,
                String.class);

        NewsResponse actualResponse = objectMapper.readValue(
                responseEntity.getBody(),
                new TypeReference<>() {
                });

        //then
        assertAll(
                () -> assertNotNull(actualResponse.id()),
                () -> assertEquals(NewsTestData.NEWS_ID_UPD, actualResponse.id()),
                () -> assertEquals(newsRequest.title(), actualResponse.title()),
                () -> assertEquals(newsRequest.text(), actualResponse.text()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );
    }


    @Test
    void shouldDeleteNewsByIdWithComments() {
        //given
        long newsId = NewsTestData.NEWS_ID;

        WireMock.stubFor(
                WireMock.delete(urlPathEqualTo("/" + newsId + "/comments"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        )
        );

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/" + newsId,
                HttpMethod.DELETE,
                null,
                String.class);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


    @Test
    void shouldGetAllNewsWithComments() throws JsonProcessingException {
        //given
        WireMock.stubFor(
                WireMock.get(urlPathEqualTo("/1/comments"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(
                                                objectMapper.writeValueAsString(
                                                        NewsTestData
                                                                .getListWithTwoCommentResponse()
                                                )
                                        )
                        )
        );

        WireMock.stubFor(
                WireMock.get(urlPathEqualTo("/2/comments"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(
                                                objectMapper.writeValueAsString(
                                                        NewsTestData
                                                                .getListWithTwoCommentResponse()
                                                )
                                        )
                        )
        );

        WireMock.stubFor(
                WireMock.get(urlPathEqualTo("/3/comments"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody("[]"))
        );


        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/comments",
                HttpMethod.GET,
                null,
                String.class);


        //when
        List<NewsCommentsResponse> actualResponse = objectMapper.readValue(
                responseEntity.getBody(),
                new TypeReference<>() {
                });

        //then
        assertAll(
                () -> assertNotNull(actualResponse),
                () -> assertEquals(3, actualResponse.size()),
                () -> assertEquals(2, actualResponse.getFirst().comments().size()),
                () -> assertEquals(2, actualResponse.get(1).comments().size()),
                () -> assertEquals(0, actualResponse.get(2).comments().size()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }


    @Test
    void shouldGetNewsByIdWithComments() throws JsonProcessingException {
        //given
        WireMock.stubFor(
                WireMock.get(urlPathEqualTo("/1/comments"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(
                                                objectMapper.writeValueAsString(
                                                        NewsTestData
                                                                .getListWithTwoCommentResponse()
                                                )
                                        )
                        )
        );

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/1/comments",
                HttpMethod.GET,
                null,
                String.class);


        //when
        NewsCommentsResponse actualResponse = objectMapper.readValue(
                responseEntity.getBody(),
                new TypeReference<>() {
                });

        //then
        assertAll(
                () -> assertNotNull(actualResponse),
                () -> assertEquals(2, actualResponse.comments().size()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }


    @Test
    void shouldNotGetNewsByIdWithComments_whenNewsIdNotFound() {
        //given
        Long newsId = NewsTestData.NEWS_ID_NOT_FOUND;

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/" + newsId + "/comments",
                HttpMethod.GET,
                null,
                String.class);


        //when, then
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }

    @Test
    void shouldGetCommentById() throws Exception {
        //given
        Long newsId = NewsTestData.NEWS_ID;
        Long commentId = NewsTestData.COMMENT_ID;

        CommentResponse commentResponse = NewsTestData.getCommentResponse();
        String jsonString = objectMapper.writeValueAsString(commentResponse);


        WireMock.stubFor(
                WireMock.get(urlPathEqualTo("/comments/" + commentId))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(jsonString))
        );

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/" + newsId + "/comments/" + commentId,
                HttpMethod.GET,
                null,
                String.class);

        CommentResponse actualResponse = objectMapper.readValue(
                responseEntity.getBody(),
                new TypeReference<>() {
                });

        //when, then
        assertAll(
                () -> assertNotNull(actualResponse),
                () -> assertEquals(NewsTestData.USERNAME, actualResponse.username()),
                () -> assertEquals(NewsTestData.NEWS_ID, actualResponse.newsId()),
                () -> assertEquals(NewsTestData.COMMENT_TEXT, actualResponse.text()),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }

    @Test
    void shouldNotGetCommentById_whenNewsIdNotFound() {
        //given
        Long newsId = NewsTestData.NEWS_ID_NOT_FOUND;
        Long commentId = NewsTestData.COMMENT_ID;

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/" + newsId + "/comments/" + commentId,
                HttpMethod.GET,
                null,
                String.class);

        //when, then
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }

    @Test
    void shouldNotGetCommentById_whenNewsIdNotMatchCommentId() throws Exception {
        //given
        Long newsId = NewsTestData.NEWS_ID_UPD;
        Long commentId = NewsTestData.COMMENT_ID;

        CommentResponse commentResponse = NewsTestData.getCommentResponse();
        String jsonString = objectMapper.writeValueAsString(commentResponse);


        WireMock.stubFor(
                WireMock.get(urlPathEqualTo("/comments/" + commentId))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(jsonString))
        );

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/" + newsId + "/comments/" + commentId,
                HttpMethod.GET,
                null,
                String.class);

        //when, then
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }

    @Test
    void shouldReturnTrueIfNewsIsExists() {
        //given
        long newsId = NewsTestData.NEWS_ID;

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/exists/" + newsId,
                HttpMethod.GET,
                null,
                String.class);

        //then
        assertAll(
                () -> assertTrue(Boolean.parseBoolean(responseEntity.getBody())),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }

    @Test
    void shouldReturnFalseIfNewsIsNotExists() {
        //given
        long newsId = NewsTestData.NEWS_ID_NOT_FOUND;

        //when
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/news/exists/" + newsId,
                HttpMethod.GET,
                null,
                String.class);

        //then
        assertAll(
                () -> assertFalse(Boolean.parseBoolean(responseEntity.getBody())),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }

    @Test
    void shouldGetNewsWithFullTextSearch() {
        //given
        String searchValue = NewsTestData.SEARCH_VALUE;
        List<String> searchFields = NewsTestData.SEARCH_FIELDS;
        int searchLimit = NewsTestData.SEARCH_LIMIT;

        //when
        URI uri = UriComponentsBuilder.fromHttpUrl(restTemplate.getRootUri())
                .path("/news/search")
                .queryParam("text", searchValue)
                .queryParam("fields", searchFields)
                .queryParam("limit", String.valueOf(searchLimit))
                .build().toUri();


        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                String.class);

        //then
        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }

    @Test
    void shouldNotGetNewsWithFullTextSearch_whenSearchFieldsIsNotValid() {

        //given
        String searchValue = NewsTestData.SEARCH_VALUE;
        List<String> searchFields = NewsTestData.SEARCH_NOT_VALID_FIELDS;
        int searchLimit = NewsTestData.SEARCH_LIMIT;

        //when
        URI uri = UriComponentsBuilder.fromHttpUrl(restTemplate.getRootUri())
                .path("/news/search")
                .queryParam("text", searchValue)
                .queryParam("fields", searchFields)
                .queryParam("limit", String.valueOf(searchLimit))
                .build().toUri();

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                String.class);

        //then
        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode()),
                () -> assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType())
        );

    }

}