package com.example.server.config.security.principal;

import com.example.server.domain.user.entity.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class SecurityPrincipal implements UserDetails {
    private final String email;
    private final String password;
    private final List<GrantedAuthority> authorities;
    private final User user; // User 객체를 추가


    // Constructor
    public SecurityPrincipal(String email, String password, List<GrantedAuthority> authorities, User user) {
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.user = user;
    }

    public User toUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
