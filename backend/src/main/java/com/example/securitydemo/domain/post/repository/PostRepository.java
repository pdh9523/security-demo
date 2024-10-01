package com.example.securitydemo.domain.post.repository;

import com.example.securitydemo.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
