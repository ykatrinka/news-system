package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.dto.request.CommentRequest;
import ru.clevertec.dto.response.CommentResponse;
import ru.clevertec.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    Comment requestToComment(CommentRequest commentRequest);

    CommentResponse commentToResponse(Comment comment);

    @Mapping(target = "id", source = "commentId")
    Comment updateFromRequest(Long commentId, CommentRequest request);
}
