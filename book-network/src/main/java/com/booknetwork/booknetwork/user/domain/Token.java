package com.booknetwork.booknetwork.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class Token {
    @Id
    @GeneratedValue
    private Long id;

    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expireTime;
    private LocalDateTime validatedAt;

    @ManyToOne
    @JoinColumn(name = "userId",nullable = false)
    private User user;

}
