package com.example.securitydemo.domain.user.repository;

import com.example.securitydemo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
