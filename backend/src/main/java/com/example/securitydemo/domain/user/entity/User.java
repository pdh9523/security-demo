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

    public static User createUser(String email, String password, String nickname, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        return user;
    }
}
