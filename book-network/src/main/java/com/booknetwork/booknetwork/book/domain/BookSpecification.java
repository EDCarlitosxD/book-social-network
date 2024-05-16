package com.booknetwork.booknetwork.book.domain;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withOwnerId(Long ownerId){
        return (root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId);
    }
}
