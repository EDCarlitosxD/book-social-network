package com.booknetwork.booknetwork.feedback;

import com.booknetwork.booknetwork.book.Feedback;
import com.booknetwork.booknetwork.book.domain.Book;
import com.booknetwork.booknetwork.book.domain.BookRepository;
import com.booknetwork.booknetwork.book.domain.OperationNotPermittedException;
import com.booknetwork.booknetwork.book.domain.PageResponse;
import com.booknetwork.booknetwork.feedback.domain.FeedbackMapper;
import com.booknetwork.booknetwork.feedback.domain.FeedbackRepository;
import com.booknetwork.booknetwork.feedback.domain.FeedbackRequest;
import com.booknetwork.booknetwork.feedback.domain.FeedbackResponse;
import com.booknetwork.booknetwork.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedBackService {
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Long saveFeedback(FeedbackRequest feedbackRequest, Authentication connectedUser) {
        Book book = bookRepository.findById(feedbackRequest.bookId())
                .orElseThrow(()->
                        new EntityNotFoundException("No book found with ID::"+feedbackRequest.bookId()));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for and archived or not shareable book");
        }
        User user = (User) connectedUser.getPrincipal();

        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot give a feedback to your own book" );
        }

        Feedback feedback = feedbackMapper.toFeedBack(feedbackRequest);
        return feedbackRepository.save(feedback).getId();
    }

    @Transactional
    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
