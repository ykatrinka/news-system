package ru.clevertec.cache.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.entity.News;
import util.NewsTestData;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LFUCacheTest {

    public static final long NEWS_ID = 1L;
    public static final long NEWS_ID_SECOND = 2L;
    public static final String CHANGED_NEWS = "Changed news";
    private LFUCache<Long, News> cache;

    @BeforeEach
    void setUp() {
        cache = new LFUCache<>();
    }

    @Test
    void shouldPutNewsInEmptyCache() {
        News news = NewsTestData.getNewsForCreate();
        news.setId(NEWS_ID);
        cache.put(news.getId(), news);

        assertNotNull(cache);
        assertTrue(cache.get(news.getId()).isPresent());
    }

    @Test
    void shouldPutNewsInNotEmptyCache() {
        News news1 = News.builder()
                .id(NEWS_ID)
                .build();
        News news2 = News.builder()
                .id(NEWS_ID_SECOND)
                .build();

        cache.put(news1.getId(), news1);
        cache.put(news2.getId(), news2);

        assertNotNull(cache);
        assertTrue(cache.get(news2.getId()).isPresent());
    }

    @Test
    void shouldPutNewsInCache_whenNewsIsExists() {
        News news1 = News.builder()
                .id(NEWS_ID)
                .build();
        News news2 = News.builder()
                .id(NEWS_ID_SECOND)
                .build();

        cache.put(news1.getId(), news1);
        cache.put(news2.getId(), news2);

        news2.setText(CHANGED_NEWS);
        cache.put(news2.getId(), news2);


        assertNotNull(cache);
        assertTrue(cache.get(news2.getId()).isPresent());
        assertEquals(CHANGED_NEWS, cache.get(news2.getId()).get().getText());
    }

    @Test
    void shouldPutNewsInNotEmptyCache_whenCapacityIsMax() {
        fillCache();
        News news = News.builder()
                .id(11L)
                .build();

        cache.put(news.getId(), news);

        assertNotNull(cache);
        assertTrue(cache.get(news.getId()).isPresent());
        assertTrue(cache.get(NEWS_ID).isEmpty());
    }

    private void fillCache() {
        News news;
        for (int i = 0; i < 10; i++) {
            news = News.builder()
                    .id((long) (i + 1))
                    .build();
            cache.put(news.getId(), news);
        }

    }

    @Test
    void shouldGetNewsInCache() {
        News news = NewsTestData.getNewsForCreate();
        news.setId(NEWS_ID);
        cache.put(news.getId(), news);

        Optional<News> actualNews = cache.get(news.getId());

        assertTrue(actualNews.isPresent());
    }

    @Test
    void shouldNotGetNewsInCache_whenNewsNotFound() {
        Optional<News> actualNews = cache.get(NEWS_ID);
        assertTrue(actualNews.isEmpty());
    }

    @Test
    void shouldDeleteNewsInCache() {
        News news = NewsTestData.getNewsForCreate();
        news.setId(NEWS_ID);
        cache.put(news.getId(), news);
        cache.delete(NEWS_ID);
        Optional<News> actualNews = cache.get(NEWS_ID);
        assertTrue(actualNews.isEmpty());
    }

    @Test
    void shouldNotDeleteNewsInCache_whenNewsNotFound() {
        cache.delete(NEWS_ID);
        Optional<News> actualNews = cache.get(NEWS_ID);
        assertTrue(actualNews.isEmpty());
    }

}