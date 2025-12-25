package org.example.onebyte.service;

import org.example.onebyte.dto.mypage.MyPageInfoResponse;
import org.example.onebyte.dto.mypage.UpdateInfoRequest;

public interface MyPageService {
    MyPageInfoResponse getInfo(Long userId);
    void updateInfo(Long userId, UpdateInfoRequest request);
}