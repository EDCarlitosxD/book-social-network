package com.booknetwork.booknetwork.book.infrastructure;

import com.booknetwork.booknetwork.book.BookTransactionHistory;
import com.booknetwork.booknetwork.book.domain.Book;
import com.booknetwork.booknetwork.book.domain.BookRequest;
import com.booknetwork.booknetwork.book.domain.BookResponse;
import com.booknetwork.booknetwork.book.domain.BorrowedBooksResponse;
import com.booknetwork.booknetwork.files.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public Book toBook(BookRequest request){
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }

    public BookResponse toBookResponse(Book book){
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().getFullname())
                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
                .build();
    }

    public BorrowedBooksResponse toBorrowedBooksResponse(BookTransactionHistory history) {
        return BorrowedBooksResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();

    }
}
