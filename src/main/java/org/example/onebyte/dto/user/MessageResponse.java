package org.example.onebyte.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageResponse {
    //회원가입, 로그인, 로그아웃 등 메세지 보내고 싶을때
    private String message;
}