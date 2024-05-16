package com.booknetwork.booknetwork.book.service;

import com.booknetwork.booknetwork.book.BookTransactionHistory;
import com.booknetwork.booknetwork.book.domain.*;
import com.booknetwork.booknetwork.book.infrastructure.BookMapper;
import com.booknetwork.booknetwork.files.FileStorageService;
import com.booknetwork.booknetwork.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.hibernate.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;;
    private final FileStorageService fileStorageService;
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

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User userAuthenticated = (User) connectedUser.getPrincipal();
        Pageable pageable  = PageRequest.of(
                page,size, Sort.by("createdDate").descending());

        Page<Book> books = bookRepository.findAllDisplayedBooks(pageable,userAuthenticated.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse).toList();


        return new PageResponse<>(
            bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()

        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User userAuthenticated = (User) connectedUser.getPrincipal();
        Pageable pageable  = PageRequest.of(
                page,size, Sort.by("createdDate").descending());

        Page<Book> books = bookRepository
                .findAll(BookSpecification.withOwnerId(userAuthenticated.getId()),pageable);

        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse).toList();


        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()

        );


    }

    public PageResponse<BorrowedBooksResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User userAuthenticated = (User) connectedUser.getPrincipal();
        Pageable pageable  = PageRequest.of(
                page,size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, userAuthenticated.getId());
        List<BorrowedBooksResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBooksResponse).toList();

        return new PageResponse<>(
            bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()

        );

    }

    public PageResponse<BorrowedBooksResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User userAuthenticated = (User) connectedUser.getPrincipal();
        Pageable pageable  = PageRequest.of(
                page,size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, userAuthenticated.getId());
        List<BorrowedBooksResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBooksResponse).toList();

        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()

        );

    }

    public Long updateShareableStatus(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                ()-> new EntityNotFoundException("Not book found with the id: "+ bookId));
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot update books shareable status");
        }

        book.setShareable(!book.isShareable());

        bookRepository.save(book);
        return bookId;
    }

    public Long updateArchivedStatus(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                ()-> new EntityNotFoundException("Not book found with the id: "+ bookId));
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot update books shareable status");
        }

        book.setShareable(!book.isArchived());

        bookRepository.save(book);
        return bookId;

    }

    public Long borrowBook(Long bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("Not book found with the id: "+ bookId));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You cannot borrow book");
        }
        User user = (User) connectedUser.getPrincipal();

        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot update books shareable status");
        }

        final boolean isAlreadyBorred = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId,user.getId());
        if(isAlreadyBorred){
            throw new OperationNotPermittedException("The request book is already borrowed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Long returnBorrowedBook(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Not book found with the id: " + bookId));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You cannot borrow book");
        }

        User user = (User) connectedUser.getPrincipal();

        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot borred or return book  your own");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository
                .findByBookIdAndUserId(bookId,user.getId()).orElseThrow(
                        () -> new OperationNotPermittedException("You did not borrow this book")
                );

        bookTransactionHistory.setReturned(true);

        return transactionHistoryRepository.save(bookTransactionHistory).getId();

    }

    public Long approveReturnBorrowedBook(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Not book found with the id: " + bookId));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You cannot borrow book");
        }

        User user = (User) connectedUser.getPrincipal();

        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot borred or return book  your own");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository
                .findByBookIdAndOwnerId(bookId,user.getId()).orElseThrow(
                        () -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its returned")
                );
        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void fileUploadCoverBook(MultipartFile file, Long bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->
                        new EntityNotFoundException("No book found with the id: " + bookId));

        User user = (User) authentication.getPrincipal();

        var bookCover = fileStorageService.saveFile(file,book,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }
}
