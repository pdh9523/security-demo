package com.example.server.interfaces.controller;

import com.example.server.config.security.principal.SecurityPrincipal;
import com.example.server.domain.user.dto.UserRequestDto;
import com.example.server.domain.user.dto.UserResponseDto;
import com.example.server.domain.user.service.UserService;
import com.example.server.util.token.dto.RefreshTokenRequestDto;
import com.example.server.util.token.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.createUser(userRequestDto));
    }

    @GetMapping("/my-info")
    public ResponseEntity<UserResponseDto> getMyInfo(@AuthenticationPrincipal SecurityPrincipal securityPrincipal) {
        return ResponseEntity.ok(userService.getMyInfo(securityPrincipal.toUser()));
    }

    @PostMapping("/token-refresh")
    public ResponseEntity<TokenResponseDto> refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        return ResponseEntity.ok(userService.tokenRefresh(refreshTokenRequestDto));
    }
}
