package org.example.onebyte.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.onebyte.dto.user.LoginRequest;
import org.example.onebyte.dto.user.RegisterRequest;
import org.example.onebyte.dto.user.TokenResponse;
import org.example.onebyte.dto.user.UserResponse;

public interface UserService {
    // 회원가입(+자동로그인 정책이면 토큰까지 발급)
    TokenResponse register(RegisterRequest request, HttpServletResponse response);

    // 로그인(토큰 발급 + refresh 쿠키 세팅)
    TokenResponse login(LoginRequest request, HttpServletResponse response);

    // 토큰 재발급(accessToken만 새로 발급)
    TokenResponse reissue(String refreshToken);

    // 로그아웃(DB refresh 삭제 + 쿠키 만료)
    void logout(String refreshToken, HttpServletResponse response);

    // refresh 쿠키만 만료(쿠키 없을 때도 로그아웃 눌렀다고 처리하려고)
    void expireRefreshCookie(HttpServletResponse response);

}
