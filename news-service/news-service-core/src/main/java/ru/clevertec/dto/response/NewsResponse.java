package ru.clevertec.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NewsResponse(
        Long id,
        LocalDateTime time,
        String title,
        String text
) {
}
