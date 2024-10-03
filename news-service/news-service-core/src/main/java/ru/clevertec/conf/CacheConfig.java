package ru.clevertec.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.cache.CustomCache;
import ru.clevertec.cache.factory.CacheType;
import ru.clevertec.cache.factory.impl.CacheFactoryImpl;
import ru.clevertec.entity.News;

@Configuration
public class CacheConfig {

    @Value("${cache.type}")
    private CacheType cacheType;

    @Bean
    public CustomCache<Long, News> cacheBean() {
        return new CacheFactoryImpl<Long, News>().getInstance(cacheType);
    }
}
