package org.example.onebyte.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.user.RegisterRequest;
import org.example.onebyte.dto.user.UserResponse;
import org.example.onebyte.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(){
        // 기능 구현
        return null;
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<UserResponse> logoutUser() {
        //기능구현
        return null;
    }

    //토큰재발급
    @PostMapping("/reissue")
    public ResponseEntity<UserResponse> reissueUser(){
        return null;
    }

}
