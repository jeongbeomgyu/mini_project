package org.example.onebyte.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.user.*;
import org.example.onebyte.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> createUser(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = userService.register(request,response);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = userService.login(request,response);
        return ResponseEntity.ok(tokenResponse);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logoutUser(@CookieValue(name="refreshToken", required = false)  String refreshToken, HttpServletResponse response) {
        userService.logout(refreshToken,response);
        return ResponseEntity.ok(new MessageResponse("로그아웃 완료"));
    }
//
//    //토큰재발급
//    @PostMapping("/reissue")
//    public ResponseEntity<UserResponse> reissueUser(){
//        return null;
//    }

}
