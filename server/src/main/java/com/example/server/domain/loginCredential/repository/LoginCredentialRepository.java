package com.example.server.domain.loginCredential.repository;

import com.example.server.domain.loginCredential.entity.LoginCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginCredentialRepository extends JpaRepository<LoginCredential, Long> {

    Optional<LoginCredential> findByEmail(String email);

    Optional<LoginCredential> findByRefreshToken(String refreshToken);

    @Query("SELECT CASE WHEN (u.refreshToken = :refreshToken) THEN true ELSE false END FROM User u WHERE u.email = :email")
    Boolean existsByRefreshTokenAndEmail(@Param("refreshToken")String refreshToken, @Param("email") String email);
}
