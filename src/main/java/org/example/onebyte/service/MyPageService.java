package org.example.onebyte.service;

import org.example.onebyte.dto.mypage.MyPageInfoResponse;
import org.example.onebyte.dto.mypage.UpdateInfoRequest;
import org.example.onebyte.dto.mypage.UpdatePasswordRequest;

public interface MyPageService {
    //정보조회
    MyPageInfoResponse getInfo(Long userId);

    //이름, 닉네임 변경
    void updateInfo(Long userId, UpdateInfoRequest request);

    //비밀번호 변경
    void updatePassword(Long userId, UpdatePasswordRequest request);

    //회원탈퇴
    void withdraw(Long userId);
}