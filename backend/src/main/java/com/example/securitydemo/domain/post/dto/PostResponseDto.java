package com.example.securitydemo.domain.post.dto;

import com.example.securitydemo.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostResponseDto(
        String title,
        String content,
        LocalDateTime createdAt
) {
    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt()
                );
    }
}
