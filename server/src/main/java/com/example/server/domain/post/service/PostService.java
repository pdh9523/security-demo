package com.example.server.domain.post.service;

import com.example.server.domain.post.dto.PostRequestDto;
import com.example.server.domain.post.dto.PostResponseDto;

import java.util.List;

public interface PostService {
    void createPost(PostRequestDto postRequestDto);

    PostResponseDto getPostById(Long id);

    List<PostResponseDto> getAllPosts();
}
