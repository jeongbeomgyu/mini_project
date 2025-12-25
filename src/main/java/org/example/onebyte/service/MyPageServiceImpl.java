package org.example.onebyte.service;

import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.mypage.MyPageInfoResponse;
import org.example.onebyte.dto.mypage.UpdateInfoRequest;
import org.example.onebyte.entity.User;
import org.example.onebyte.exception.AuthenticationFailedException;
import org.example.onebyte.exception.DuplicateResourceException;
import org.example.onebyte.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public MyPageInfoResponse getInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationFailedException("유저 없음"));
        return MyPageInfoResponse.from(user);
    }

    @Override
    @Transactional
    public void updateInfo(Long userId, UpdateInfoRequest request){
        User user = userRepository.findById(userId).orElseThrow(()->new AuthenticationFailedException("유저가 존재하지 않습니다."));

        // trim : 앞뒤공백날림
        String newName = (request.getName() == null || request.getName().isBlank()) ? user.getName() : request.getName().trim();
        String newNickname = (request.getNickname() == null || request.getNickname().isBlank()) ? user.getNickname() : request.getNickname().trim();

        //닉네임만 중복체크
        if (!newNickname.equals(user.getNickname())&& userRepository.existsByNickname(newNickname)) {
            throw DuplicateResourceException.userNickname(newNickname);
        }

        // 변경 없으면 종료
        if (newName.equals(user.getName()) && newNickname.equals(user.getNickname())) {
            return;
        }

        user.changeInfo(newName, newNickname);
    }





}
