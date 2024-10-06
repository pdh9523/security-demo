package com.example.server.domain.post.service;

import com.example.server.domain.post.dto.PostRequestDto;
import com.example.server.domain.post.dto.PostResponseDto;
import com.example.server.domain.post.entity.Post;
import com.example.server.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public void createPost(PostRequestDto postRequestDto) {
        postRepository.save(Post.createPost(postRequestDto));
    }

    @Override
    public PostResponseDto getPostById(Long postId) {
        return PostResponseDto.from(postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다.")));
    }

    @Override
    public List<PostResponseDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostResponseDto::from).toList();
    }
}
