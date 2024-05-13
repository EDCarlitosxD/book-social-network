package com.booknetwork.booknetwork.book.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;

public record BookRequest(
        Long id,
        @NotBlank(message = "100")
        @NotNull(message = "100")
        String title,
        @NotBlank(message = "101")
        @NotNull(message = "101")
        String authorName,

        @NotBlank(message = "102")
        @NotNull(message = "102")
        String isbn,
        @NotBlank(message = "103")
        @NotNull(message = "103")
        String synopsis,
        boolean shareable
) {

}
