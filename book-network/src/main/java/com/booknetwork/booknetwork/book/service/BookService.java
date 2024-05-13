package com.booknetwork.booknetwork.book.service;

import com.booknetwork.booknetwork.book.Book;
import com.booknetwork.booknetwork.book.domain.BookRepository;
import com.booknetwork.booknetwork.book.domain.BookRequest;
import com.booknetwork.booknetwork.book.domain.BookResponse;
import com.booknetwork.booknetwork.book.infrastructure.BookMapper;
import com.booknetwork.booknetwork.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    public Long save(BookRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookMapper.toBook(request);
        book.setOwner(user);

        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Long bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse )
                .orElseThrow(()->new EntityNotFoundException("Not book found with the id: "+ bookId));
    }
}
