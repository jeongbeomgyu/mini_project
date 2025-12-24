package org.example.onebyte.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.onebyte.type.Role;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NICKNAME", nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 20)
    private Role role;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive;

    //CREATED_AT, UPDATE_AT 자동 업데이트
    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    //회원가입용 생성 메서드
    public static User createForRegister(String  nickname, String email, String passwordHash) {
        return new User(
                null,
                nickname,
                email,
                passwordHash,
                Role.ROLE_USER,
                true,
                null,
                null
        );
    }

    //도메인 메서드
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void deactivate() { // 회원 비활성화(삭제 대신)
        this.isActive = false;
    }
}
