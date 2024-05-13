package com.booknetwork.booknetwork.book.domain;

import com.booknetwork.booknetwork.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
}
