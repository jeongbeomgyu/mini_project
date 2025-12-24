package org.example.onebyte.service;

import org.example.onebyte.dto.user.RegisterRequest;
import org.example.onebyte.dto.user.UserResponse;

public interface UserService {

    // 회원가입
    UserResponse register(RegisterRequest request);
//
//    // 로그인
//    UserResponse.LoginResponse login(UserRequest.LoginRequest request);
//    // 토큰 재발급
//    UserResponse.ReissueResponse reissueToken(UserRequest.ReissueRequest request);
//    // 로그아웃
//    void logout(UserRequest.LogoutRequest request, Long userId);
}
