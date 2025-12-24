package org.example.onebyte.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.user.*;
import org.example.onebyte.entity.RefreshToken;
import org.example.onebyte.entity.User;
import org.example.onebyte.exception.AuthenticationFailedException;
import org.example.onebyte.exception.DuplicateResourceException;
import org.example.onebyte.repository.RefreshTokenRepository;
import org.example.onebyte.repository.UserRepository;
import org.example.onebyte.security.JwtTokenizer;
import org.example.onebyte.util.CookieUtil;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtil cookieUtil;
    private final JwtTokenizer jwtTokenizer;

    //추후 변경가능
    private long refreshMaxAgeSeconds = 24 * 60 * 60L;

    // 회원가입
    @Override
    public MessageResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw DuplicateResourceException.userEmail(request.getEmail());
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw DuplicateResourceException.userNickname(request.getNickname());
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        User user = User.createForRegister(
                request.getName(),
                request.getNickname(),
                request.getEmail(),
                passwordHash
        );

        userRepository.save(user);

        return new MessageResponse("회원가입 완료");
    }



    // 로그인
    @Override
    public TokenResponse login(LoginRequest request, HttpServletResponse response) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationFailedException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 비활성 유저도 로그인 실패(메시지 통일)
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new AuthenticationFailedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationFailedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 토큰 생성
        String accessToken = jwtTokenizer.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getRole()
        );

        String refreshToken = jwtTokenizer.createRefreshToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getRole()
        );

        refreshTokenRepository.deleteByUser_Id(user.getId());
        // 충돌 방지
        refreshTokenRepository.flush();

        //추후 DB에서 직접 createdAt 생성될때 거기서 자동 할당 되는 방법 없는지 고려
        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshMaxAgeSeconds))
                .build();

        refreshTokenRepository.save(rt);

        // refreshToken 쿠키 세팅 (CookieUtil 사용)
        // **** 로그인 응답에 Set-Cookie  보냄
        cookieUtil.addRefreshTokenCookie(response, refreshToken, refreshMaxAgeSeconds);

        // 바디에는 accessToken만 내려줌
        return new TokenResponse(accessToken);
    }


    // 로그아웃(DB refresh 삭제 + 쿠키 만료)
    @Override
    public ResponseEntity<MessageResponse> logout(String refreshToken, HttpServletResponse response){

        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
        cookieUtil.addRefreshTokenCookie(response,refreshToken,refreshMaxAgeSeconds);

        return ResponseEntity.ok(new MessageResponse("로그아웃 완료"));
    }

    // 토큰 재발급(accessToken만 새로 발급)
    @Override
    public TokenResponse reissue(String refreshToken){
//        1.refreshToken 값이 있는지 체크
        if(refreshToken != null && !refreshToken.isBlank()){
            throw new AuthenticationFailedException("로그인이 필요합니다.");
        }
//        2.DB에서 refreshToken 레코드 조회
        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken).orElseThrow(()->new AuthenticationFailedException("로그인이 필요합니다."));

//        3.만료됐는지 체크
        if(rt.isExpired()){
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new AuthenticationFailedException("로그인이 필요합니다.");
        }
//        4.연결된 유저
        User user = rt.getUser();
//        5.새 accessToken 발급해서 TokenResponse로 반환
        String newAccessToken = jwtTokenizer.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getRole()
        );

        return new TokenResponse(newAccessToken);
    }

}
