package org.example.onebyte.service;

import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.mypage.MyPageInfoResponse;
import org.example.onebyte.dto.mypage.UpdateInfoRequest;
import org.example.onebyte.dto.mypage.UpdatePasswordRequest;
import org.example.onebyte.entity.User;
import org.example.onebyte.exception.AuthenticationFailedException;
import org.example.onebyte.exception.DuplicateResourceException;
import org.example.onebyte.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //정보조회
    @Override
    @Transactional(readOnly = true)
    public MyPageInfoResponse getInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationFailedException("유저 없음"));
        return MyPageInfoResponse.from(user);
    }

    //이름, 닉네임변경
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

    //비밀번호 변경
    @Override
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationFailedException("존재하지 않는 사용자 입니다."));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
            throw new AuthenticationFailedException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(req.getNewPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("기존 비밀번호와 동일합니다.");
        }

        String newPasswordHash = passwordEncoder.encode(req.getNewPassword());
        user.changePasswordHash(newPasswordHash);
    }
}
