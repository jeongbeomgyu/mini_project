package org.example.onebyte.controller;

import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.mypage.MyPageInfoResponse;
import org.example.onebyte.dto.mypage.UpdateInfoRequest;
import org.example.onebyte.dto.user.MessageResponse;
import org.example.onebyte.security.JwtTokenizer;
import org.example.onebyte.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;
    private final JwtTokenizer jwtTokenizer;

    //정보 조회
    @GetMapping("/info")
    public ResponseEntity<MyPageInfoResponse> getMyInfo(@RequestHeader("Authorization") String authorization) {
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);
        return ResponseEntity.ok(myPageService.getInfo(userId));
    }

    //정보 수정(이름, 닉네임)
    @PatchMapping("/update")
    public ResponseEntity<MessageResponse> updateInfo(@RequestHeader("Authorization") String authorization, @RequestBody UpdateInfoRequest request) {
        Long userId = jwtTokenizer.getUserIdFromToken(authorization);
        myPageService.updateInfo(userId, request);
        return ResponseEntity.ok(new MessageResponse("정보가 수정되었습니다."));
    }

    //정보 수정(비밀번호)



}
