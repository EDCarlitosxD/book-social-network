package com.booknetwork.booknetwork.book.domain;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    private long totalPage;
    private boolean first;
    private boolean last;

}
