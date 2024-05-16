package com.booknetwork.booknetwork.feedback;

import com.booknetwork.booknetwork.book.domain.PageResponse;
import com.booknetwork.booknetwork.feedback.domain.FeedbackRequest;
import com.booknetwork.booknetwork.feedback.domain.FeedbackResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedback")
@RequiredArgsConstructor
@Tag(name = "feedback")
public class FeedBackController {

    private final FeedBackService feedBackService;

    @PostMapping
    public ResponseEntity<Long> saveFeedBack(
            @RequestBody FeedbackRequest feedback,
            Authentication connectedUser) {
        return ResponseEntity.ok(feedBackService.saveFeedback(feedback,connectedUser));
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedBackService.findAllFeedbacksByBook(bookId, page, size, connectedUser));
    }
}
