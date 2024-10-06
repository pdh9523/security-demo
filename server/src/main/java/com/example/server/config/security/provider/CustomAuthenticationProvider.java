package com.example.server.config.security.provider;

import com.example.server.config.security.principal.SecurityPrincipal;
import com.example.server.domain.loginCredential.service.LoginCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final LoginCredentialService loginCredentialService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        String email = token.getName();
        String password = (String) token.getCredentials();

        SecurityPrincipal securityPrincipal = loginCredentialService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, securityPrincipal.getPassword())) {
            throw new BadCredentialsException("비밀번호가 틀렸습니다.");
        }

        return new UsernamePasswordAuthenticationToken(securityPrincipal, null, securityPrincipal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
