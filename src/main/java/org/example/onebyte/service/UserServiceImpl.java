package org.example.onebyte.service;

import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.user.RegisterRequest;
import org.example.onebyte.dto.user.UserResponse;
import org.example.onebyte.entity.User;
import org.example.onebyte.exception.DuplicateResourceException;
import org.example.onebyte.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw DuplicateResourceException.userEmail(request.getEmail());
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw DuplicateResourceException.userNickname(request.getNickname());
        }

        //비밀번호 해시
        String passwordHash = passwordEncoder.encode(request.getPassword());

        //엔티티 생성 (role=ROLE_USER, isActive=true 고정)
        User user = User.createForRegister(
                request.getNickname(),
                request.getEmail(),
                passwordHash
        );

        User saved = userRepository.save(user);

        //응답 DTO 변환
        return UserResponse.from(saved);
    }
//
//    @Override
//    public UserResponse.LoginResponse login(UserRequest.LoginRequest request) {}
//
//    @Override
//    public UserResponse.ReissueResponse reissueToken(UserRequest.ReissueRequest request){}
//
//    @Override
//    public void logout(UserRequest.LogoutRequest request, Long userId) {}
}
