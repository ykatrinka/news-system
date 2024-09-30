package ru.clevertec.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Builder
public record NewsRequest(
        LocalDateTime time,
        @NotBlank
        @Length(min = 1, max = 255)
        String title,
        @NotBlank
        String text
) {
}
