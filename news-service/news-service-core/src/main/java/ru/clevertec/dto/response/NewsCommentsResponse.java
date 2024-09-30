package ru.clevertec.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NewsCommentsResponse(
        Long id,
        LocalDateTime time,
        String title,
        String text,
        List<CommentResponse> comments
) {
}
