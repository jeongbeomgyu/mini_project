package org.example.onebyte.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.mypage.MyPageInfoResponse;
import org.example.onebyte.dto.mypage.UpdateInfoRequest;
import org.example.onebyte.dto.mypage.UpdatePasswordRequest;
import org.example.onebyte.dto.user.MessageResponse;
import org.example.onebyte.entity.User;
import org.example.onebyte.exception.AuthenticationFailedException;
import org.example.onebyte.exception.DuplicateResourceException;
import org.example.onebyte.repository.UserRepository;
import org.example.onebyte.security.JwtTokenizer;
import org.example.onebyte.service.MyPageService;
import org.example.onebyte.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;
    private final JwtTokenizer jwtTokenizer;
    private final CookieUtil cookieUtil;

    //정보 조회
    @GetMapping("/info")
    public ResponseEntity<MyPageInfoResponse> getMyInfo(@RequestHeader("Authorization") String authorization) {
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);
        return ResponseEntity.ok(myPageService.getInfo(userId));
    }

    //정보 수정(이름, 닉네임)
    @PatchMapping("/info")
    public ResponseEntity<MessageResponse> updateInfo(@RequestHeader("Authorization") String authorization, @RequestBody UpdateInfoRequest request) {
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);
        myPageService.updateInfo(userId, request);
        return ResponseEntity.ok(new MessageResponse("정보가 수정되었습니다."));
    }

    //정보 수정(비밀번호)
    @PatchMapping("/password")
    public ResponseEntity<MessageResponse> updatePassword(@RequestHeader("Authorization") String authorization, @Valid @RequestBody UpdatePasswordRequest request) {
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);
        myPageService.updatePassword(userId, request);
        return ResponseEntity.ok(new MessageResponse("비밀번호가 변경되었습니다."));
    }

    //물리적으로 db삭제가 아니더라도 이런식의 탈퇴는 Delete로 처리하는게 맞다고 함.
    //회원가입 부분 수정 필요
    //회원 탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdraw(@RequestHeader("Authorization") String authorization, HttpServletResponse response) {
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);

        myPageService.withdraw(userId);

        //쿠키도 만료시켜서 브라우저에 남은 refreshToken 제거
        cookieUtil.expireRefreshTokenCookie(response);

        return ResponseEntity.ok(new MessageResponse("회원탈퇴가 완료되었습니다."));
    }

    // 내 게시물 조회





    // 내 댓글 조회




}
