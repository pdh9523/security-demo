package com.example.server.domain.post.entity;

import com.example.server.domain.global.entity.BaseTimeEntity;
import com.example.server.domain.post.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "posts")
@Inheritance(strategy = InheritanceType.JOINED)
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    public static Post createPost(PostRequestDto postRequestDto) {
        Post post = new Post();
        post.setTitle(postRequestDto.title());
        post.setContent(postRequestDto.content());
        return post;
    }
}
