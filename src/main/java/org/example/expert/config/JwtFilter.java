package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String url = request.getRequestURI();

        if (isSwaggerUrl(url) || url.startsWith("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        String bearerJwt = request.getHeader("Authorization");

        if (bearerJwt == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
            return;
        }

        try {
            String jwt = jwtUtil.substringToken(bearerJwt);
            Claims claims = jwtUtil.extractClaims(jwt);

            if (claims == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            setAuthentication(claims, request);
            chain.doFilter(request, response);

        } catch (Exception e) {
            handleException(response, e);
        }
    }

    private void setAuthentication(Claims claims, HttpServletRequest request) {
        UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

        // 기존 attribute 설정 유지
        request.setAttribute("userId", Long.parseLong(claims.getSubject()));
        request.setAttribute("email", claims.get("email"));
        request.setAttribute("userRole", claims.get("userRole"));
        request.setAttribute("nickname", claims.get("nickname"));

        // Spring Security 인증 설정
        List<GrantedAuthority> authorities =
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole.name()));

        Authentication authentication =
            new UsernamePasswordAuthenticationToken(claims.get("email"), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        if (e instanceof SecurityException || e instanceof MalformedJwtException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 서명입니다.");
        } else if (e instanceof ExpiredJwtException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } else if (e instanceof UnsupportedJwtException) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isSwaggerUrl(String url) {
        return url.contains("swagger-ui") ||
            url.contains("api-docs") ||
            url.contains("webjars") ||
            url.contains("/swagger-resources") ||
            url.contains("/h2-console") ||
            url.equals("/");
    }
}