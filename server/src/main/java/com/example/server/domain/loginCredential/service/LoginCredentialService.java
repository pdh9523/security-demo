package com.example.server.domain.loginCredential.service;

import com.example.server.config.security.principal.SecurityPrincipal;
import com.example.server.domain.global.enums.UserType;
import com.example.server.domain.loginCredential.entity.LoginCredential;
import com.example.server.domain.loginCredential.repository.LoginCredentialRepository;
import com.example.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class LoginCredentialService implements UserDetailsService {

    private final LoginCredentialRepository loginCredentialRepository;

    @Override
    public SecurityPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
        LoginCredential loginCredential = loginCredentialRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디 입니다."));

        GrantedAuthority authority = new SimpleGrantedAuthority(UserType.USER.toString()); // 기본 권한 설정
        return new SecurityPrincipal(loginCredential.getEmail(), loginCredential.getPassword(), Collections.singletonList(authority), (User) loginCredential);
    }
}
