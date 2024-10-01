package com.example.securitydemo.domain.post.service;

import com.example.securitydemo.domain.post.dto.PostRequestDto;
import com.example.securitydemo.domain.post.dto.PostResponseDto;

import java.util.List;

public interface PostService {
    void createPost(PostRequestDto postRequestDto);

    PostResponseDto getPostById(Long id);

    List<PostResponseDto> getAllPosts();
}
