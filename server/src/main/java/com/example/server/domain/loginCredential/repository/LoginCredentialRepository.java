package com.example.server.domain.loginCredential.repository;

import com.example.server.domain.loginCredential.entity.LoginCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginCredentialRepository extends JpaRepository<LoginCredential, Long> {

    Optional<LoginCredential> findByEmail(String email);

    Optional<LoginCredential> findByRefreshToken(String refreshToken);
}
