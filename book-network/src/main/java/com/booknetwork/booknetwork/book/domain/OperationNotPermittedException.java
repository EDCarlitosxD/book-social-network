package com.booknetwork.booknetwork.book.domain;

public class OperationNotPermittedException extends RuntimeException {

    public OperationNotPermittedException(String s) {
        super(s);
    }
}
