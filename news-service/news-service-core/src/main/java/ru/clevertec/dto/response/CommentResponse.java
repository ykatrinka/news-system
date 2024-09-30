package ru.clevertec.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponse(
        Long id,
        LocalDateTime time,
        String text,
        String username,
        Long newsId
) {

}




