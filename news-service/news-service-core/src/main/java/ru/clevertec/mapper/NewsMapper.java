package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.dto.request.NewsRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.dto.response.NewsCommentsResponse;
import ru.clevertec.dto.response.NewsResponse;
import ru.clevertec.entity.News;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    @Mapping(target = "id", ignore = true)
    News requestToNews(NewsRequest request);

    NewsResponse newsToResponse(News news);

    @Mapping(target = "id", source = "newsId")
    News updateFromRequest(Long newsId, NewsRequest request);

    @Mapping(target = "comments", source = "comments")
    NewsCommentsResponse newsToCommentsResponse(News news, List<CommentResponse> comments);
}
