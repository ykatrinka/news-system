package ru.clevertec.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.conf.NewsFeignConfiguration;

@FeignClient(
        name = "${spring.feign.client.news.name}",
        url = "${spring.feign.client.news.url}",
        configuration = NewsFeignConfiguration.class
)
public interface NewsFeignClient {

    @GetMapping(path = "/news/exists/{newsId}")
    Boolean isExistsNews(@PathVariable("newsId") Long newsId);
}
