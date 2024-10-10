package ru.clevertec.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.conf.NewsFeignConfiguration;

@FeignClient(
        name = "${spring.feign.client.news-service.name}",
        url = "${spring.feign.client.news-service.url}",
        configuration = NewsFeignConfiguration.class
)
public interface NewsFeignClient {

    @GetMapping(path = "/news/exists/{newsId}")
    ResponseEntity<Boolean> isExistsNews(@PathVariable("newsId") Long newsId);
}
