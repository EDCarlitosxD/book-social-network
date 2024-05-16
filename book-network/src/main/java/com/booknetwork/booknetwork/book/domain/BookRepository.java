package com.booknetwork.booknetwork.book.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book,Long>, JpaSpecificationExecutor<Book> {

    @Query("""
           SELECT book
           FROM Book book
           where book.archived  = false 
           AND  book.shareable = true 
           and book.owner.id != :id
    """)
    Page<Book> findAllDisplayedBooks(Pageable pageable, Long id);
}
