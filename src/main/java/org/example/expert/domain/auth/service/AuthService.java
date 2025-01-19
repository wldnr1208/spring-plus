package org.example.expert.domain.auth.service;

import javax.naming.AuthenticationException;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.config.security.SecurityUser;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        User newUser = new User(
            signupRequest.getEmail(),
            encodedPassword,
            userRole,
            signupRequest.getNickname()
        );
        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(),
            userRole, savedUser.getNickname());

        return new SignupResponse(bearerToken);
    }

    public SigninResponse signin(SigninRequest signinRequest) {
        try {
            // Spring Security의 Authentication 객체 생성
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    signinRequest.getEmail(),
                    signinRequest.getPassword()
                )
            );

            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            String bearerToken = jwtUtil.createToken(
                securityUser.getId(),
                securityUser.getEmail(),
                securityUser.getUserRole(),
                securityUser.getNickname()
            );

            return new SigninResponse(bearerToken);
        } catch (BadCredentialsException e) {
            throw new AuthException("잘못된 비밀번호입니다.");
        } catch (UsernameNotFoundException e) {
            throw new InvalidRequestException("가입되지 않은 유저입니다.");
        }
    }

}