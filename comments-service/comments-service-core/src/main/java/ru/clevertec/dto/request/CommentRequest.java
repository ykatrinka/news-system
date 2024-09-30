package ru.clevertec.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Builder
public record CommentRequest(

        @NotNull
        @Length(min = 1, max = 50)
        String username,

        @NotNull
        Long newsId,

        LocalDateTime time,

        @NotBlank
        String text
) {
}




