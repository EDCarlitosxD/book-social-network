package com.booknetwork.booknetwork.book.infrastructure;

import com.booknetwork.booknetwork.book.Book;
import com.booknetwork.booknetwork.book.domain.BookRequest;
import com.booknetwork.booknetwork.book.domain.BookResponse;
import com.booknetwork.booknetwork.book.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Long> addBook(
            @Valid  @RequestBody BookRequest book,
            Authentication authentication)
    {
        return ResponseEntity.ok(bookService.save(book,authentication));
    }


    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(
            @PathVariable("book-id") Long bookId){
        return ResponseEntity.ok(bookService.findById(bookId));
    }

}
