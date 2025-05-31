package com.eyedia.eyedia.service.impl;
import com.eyedia.eyedia.domain.enums.Gender;
import com.eyedia.eyedia.config.jwt.JwtProvider;
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
    private final JwtProvider jwtProvider;

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
    public String login(UserLoginDTO dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(dto.getPw(), user.getPw())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return jwtProvider.generateToken(user.getUsersId());
    }

}
