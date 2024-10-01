package com.example.securitydemo.domain.loginCredential.entity;

import com.example.securitydemo.domain.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "login_credentials")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class LoginCredential extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String email;
    @NotNull
    private String password;

    private String refreshToken;
}
