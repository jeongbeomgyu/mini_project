package org.example.onebyte.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onebyte.exception.JwtExceptionCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request 에서 토큰 얻어오기
        String token = getToken(request);

        // 토큰이 유효한지 확인하고 파싱하기
        if (StringUtils.hasText(token)) {
            try {
                getAuthentication(token);
            } catch (ExpiredJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
                log.error("Expired Token : {}", token, e);
                throw new BadCredentialsException("Expired token exception", e);
            } catch (UnsupportedJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
                log.error("Unsupported Token : {}", token, e);
                throw new BadCredentialsException("Unsupported token exception", e);
            } catch (MalformedJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
                log.error("Invalid Token : {}", token, e);
                throw new BadCredentialsException("Invalid token exception", e);
            } catch (IllegalArgumentException e) {
                request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
                log.error("Not Found Token : {}", token, e);
                throw new BadCredentialsException("Not Found Token exception", e);
            } catch (Exception e) {
                log.error("JWT Filter = Internal Error: {} ", token, e);
                throw new BadCredentialsException("Jwt Filter Internal exception", e);
            }
        }

        filterChain.doFilter(request, response);

    }

    // 토큰을 얻어오는 메소드
    private String getToken(HttpServletRequest request) {

        // 헤더에 access 토큰이 있을 경우
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        // 쿠키로 access 토큰이 들어왔을 때
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // 토큰 파싱 & 파싱해서 Claim 에 있는 정보를 꺼내서 멀티스레드에 저장하는 메소드
    private void getAuthentication(String token) {
        Claims claims = jwtTokenizer.parseAccessToken(token);
        String email = claims.getSubject();
        String name = claims.get("name", String.class);
        String nickname = claims.get("nickname", String.class);
        Long userId = claims.get("userId", Long.class);

        List<GrantedAuthority> authorities = getAuthorities(claims);

        CustomUserDetails customUserDetails = new CustomUserDetails(email, nickname, "", name,
                userId, authorities);

        Authentication authentication = new JwtAuthenticationToken(authorities, customUserDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    // 권한 정보를 List<String> 에서 List<GrantedAuthority> 로 바꾸는 메소드
    private List<GrantedAuthority> getAuthorities(Claims claims) {
        String rolesName = claims.get("roles", String.class);
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (rolesName != null) {
            authorities.add(new SimpleGrantedAuthority(rolesName));
        }

        return authorities;
    }
}
