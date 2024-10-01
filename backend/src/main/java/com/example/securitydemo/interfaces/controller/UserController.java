package com.example.securitydemo.interfaces.controller;

import com.example.securitydemo.domain.user.dto.UserRequestDto;
import com.example.securitydemo.domain.user.dto.UserResponseDto;
import com.example.securitydemo.domain.user.entity.User;
import com.example.securitydemo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.createUser(userRequestDto));
    }

    @GetMapping("/my-info")
    public ResponseEntity<UserResponseDto> getMyInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getMyInfo(user));
    }

}
