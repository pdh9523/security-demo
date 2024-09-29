package com.example.securitydemo.domain.user.entity;

import com.example.securitydemo.domain.loginCredential.entity.LoginCredential;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
@PrimaryKeyJoinColumn(name = "id")
public class User extends LoginCredential {

    @NotNull
    private String nickname;

    @Builder
    public User(String email, String password, String nickname) {
        this.setEmail(email);  // 부모 클래스의 필드 사용
        this.setPassword(password); // 부모 클래스의 필드 사용
        this.nickname = nickname;
    }

    public static User createUser(String email, String password, String nickname, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();
    }
}
