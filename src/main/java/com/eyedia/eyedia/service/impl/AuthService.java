package com.eyedia.eyedia.service.impl;
import com.eyedia.eyedia.domain.enums.Gender;

import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.dto.UserSignupDTO;
import com.eyedia.eyedia.dto.UserLoginDTO;
import com.eyedia.eyedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void signup(UserSignupDTO dto) {
        if (userRepository.existsById(dto.getId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
                .name(dto.getName())
                .age(dto.getAge())
                .gender(Gender.valueOf(dto.getGender().toUpperCase()))
                .profileImage(dto.getProfileImage())
                .id(dto.getId())
                .pw(passwordEncoder.encode(dto.getPw()))
                .currentLocation(dto.getCurrentLocation())
                .build();

        userRepository.save(user);
    }

    // 로그인
    public boolean login(UserLoginDTO dto) {
        User user = userRepository.findById(dto.getId())
                .orElse(null);

        if (user == null) return false;

        return passwordEncoder.matches(dto.getPw(), user.getPw());
    }
}
