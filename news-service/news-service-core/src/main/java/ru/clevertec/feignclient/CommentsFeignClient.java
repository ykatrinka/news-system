package ru.clevertec.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.clevertec.conf.CommentsFeignConfiguration;
import ru.clevertec.dto.response.CommentResponse;

import java.util.List;

@FeignClient(
        name = "${feign.client.news.name}",
        url = "${feign.client.news.url}",
        configuration = {CommentsFeignConfiguration.class}
)
public interface CommentsFeignClient {

    @GetMapping(path = "/{newsId}/comments")
    List<CommentResponse> getCommentsByNewsId(@PathVariable("newsId") Long newsId,
                                              @RequestParam(
                                                      name = "pageNumber",
                                                      defaultValue = "1",
                                                      required = false) int pageNumber);

    @GetMapping(path = "/comments/{commentId}")
    CommentResponse getCommentById(@PathVariable("commentId") Long commentId);

    @DeleteMapping(path = "{newsId}/comments")
    void deleteCommentsByNewsId(@PathVariable("newsId") Long newsId);
}
