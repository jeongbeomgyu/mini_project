package org.example.onebyte.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.user.LoginRequest;
import org.example.onebyte.dto.user.RegisterRequest;
import org.example.onebyte.dto.user.TokenResponse;
import org.example.onebyte.dto.user.UserResponse;
import org.example.onebyte.entity.RefreshToken;
import org.example.onebyte.entity.User;
import org.example.onebyte.exception.AuthenticationFailedException;
import org.example.onebyte.exception.DuplicateResourceException;
import org.example.onebyte.repository.RefreshTokenRepository;
import org.example.onebyte.repository.UserRepository;
import org.example.onebyte.security.JwtTokenizer;
import org.example.onebyte.util.CookieUtil;
import org.springframework.http.ResponseCookie;
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

    @Override
    public TokenResponse register(RegisterRequest request, HttpServletResponse response) {
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
        User saved = userRepository.save(user);

        //토큰 생성 (추후 JwtUtil 메서드명에 맞춰 수정)
        String accessToken = jwtTokenizer.createAccessToken(saved);
        String refreshToken = jwtTokenizer.createRefreshToken(saved);

        //refreshToken DB 저장(유저당 1개 정책)
        refreshTokenRepository.deleteByUser_Id(saved.getId());

        RefreshToken rt = RefreshToken.builder()
                .user(saved)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusDays(7)) // 예: 7일 (너희 정책대로)
                .build();

        //refreshToken DB저장
        refreshTokenRepository.save(rt);

        // 클라이언트가 refreshtoken를 쿠키로 가지고 있도록
        // 작성요망

        // accessToken은 바디로
        return new TokenResponse(accessToken);
    }

    // 로그인(토큰 발급 + refresh 쿠키 세팅)
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

        String accessToken = jwtTokenizer.createAccessToken(user);
        String refreshToken = jwtTokenizer.createRefreshToken(user);

        // refreshToken DB 저장(유저당 1개 정책)
        refreshTokenRepository.deleteByUser_Id(user.getId());

        // 범규님 내용 합하면서 수정 예정
        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusDays(7)) // 7일
                .build();

        refreshTokenRepository.save(rt);

        // 추후 수정 예정
        // 쿠키 추가 부분

        return new TokenResponse(accessToken);
    }

    // 로그아웃(DB refresh 삭제 + 쿠키 만료)
    @Override
    public void logout(String refreshToken, HttpServletResponse response){
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
        expireRefreshCookie(response);
    }

    // 토큰 재발급(accessToken만 새로 발급)
    @Override
    public TokenResponse reissue(String refreshToken){
//        1.refreshToken 값이 있는지 체크
        if(refreshToken != null && !refreshToken.isBlank()){
            throw new AuthenticationFailedException("로그인이 필요합니다.");
        }
//        2.DB에서 refreshToken 레코드 조회
        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken).orElseThrow(()->new AuthenticationFailedException("로그인이 필요합니다."))

//        3.만료됐는지 체크
        if(rt.isExpired()){
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new AuthenticationFailedException("로그인이 필요합니다.");
        }
//        4.연결된 유저
        User user = rt.getUser();
//        5.새 accessToken 발급해서 TokenResponse로 반환
        String newAccessToken = jwtTokenizer.createAccessToken(user);

        return new TokenResponse(newAccessToken);
    }


    // refresh 쿠키만 만료
    @Override
    public void expireRefreshCookie(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0) //쿠키 삭제
                .sameSite("Lax")
                // .secure(true) // HTTPS 배포환경에서 켜기
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

}
