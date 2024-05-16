package com.booknetwork.booknetwork.book.domain;

import com.booknetwork.booknetwork.book.BookTransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {
    @Query("""
        SELECT history
        FROM BookTransactionHistory history
        WHERE history.user.id = :id
    """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Long id);

    @Query("""
        SELECT history
        FROM BookTransactionHistory history
        WHERE history.user.id = :id
        AND history.returned = true
    """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Long id);

    @Query("""
        SELECT 
        (count (*) > 0) AS isBorrowed
        FROM BookTransactionHistory  bookTransactionHistory
        where bookTransactionHistory.user.id = :userId
        AND bookTransactionHistory.book.id = :bookId
        AND bookTransactionHistory.returnApproved = false 
    """)
    boolean isAlreadyBorrowedByUser(Long bookId,Long userId);

    @Query("""
        SELECT transacion
        FROM BookTransactionHistory transacion
        WHERE transacion.user.id = :userId
        AND transacion.book.id = :bookId
        AND transacion.returnApproved = false
        AND transacion.returned = false
    """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Long bookId, Long userId);

    @Query("""
        SELECT transacion
        FROM BookTransactionHistory transacion
        WHERE transacion.book.owner.id = :userID
        AND transacion.book.id = :bookId
        AND transacion.returned = true
        AND transacion.returnApproved = false
    """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Long bookId, Long userID);
}
