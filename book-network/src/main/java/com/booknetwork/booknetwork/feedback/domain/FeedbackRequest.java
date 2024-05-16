package com.booknetwork.booknetwork.feedback.domain;

import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;

public record FeedbackRequest(
        @Positive(message = "200")
        @Min(value = 0, message = "201")
        @Max(value = 5, message = "202")
        Double note,
        @NotNull(message = "203")
        @NotEmpty(message = "203")
        @NotBlank(message = "203")
        String comment,

        @NotNull(message = "204")
        Long bookId


) {

}
