package com.eyedia.eyedia.service.impl;

import com.eyedia.eyedia.config.jwt.JwtProvider;
import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.domain.enums.Gender;
import com.eyedia.eyedia.dto.UserDTO;
import com.eyedia.eyedia.dto.UserLoginDTO;
import com.eyedia.eyedia.dto.UserLoginResponseDTO;
import com.eyedia.eyedia.dto.UserSignupDTO;
import com.eyedia.eyedia.global.error.exception.GeneralException;
import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.repository.UserRepository;
import com.eyedia.eyedia.service.ExhibitionCommandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final ExhibitionCommandService exhibitionCommandService;

    // 회원가입
    public void signup(UserSignupDTO dto) {
        if (userRepository.existsById(dto.getId())) {
            throw new GeneralException(ErrorStatus.ALREADY_USER_ID_EXISTS);
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
        user.setSelectedKeywords(dto.getKeywords());
        userRepository.save(user);
    }

    // 로그인
    public UserLoginResponseDTO login(UserLoginDTO dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow( () -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getPw(), user.getPw())) {
            throw new GeneralException(ErrorStatus.WRONG_PASSWORD);
        }
        // 첫 로그인인 경우
        boolean isFist;
        if (user.isFirstLogin()) {
            isFist = true;
            user.setIsFirstLogin(false);
        } else {
            isFist = false;
        }
        // 토큰 생성
        var jwt = jwtProvider.generateToken(user.getUsersId());

        // 메인페이지 용 개인정보
        var name = user.getName() == null ? "미정" : user.getName();
        var monthlyVisitCount = exhibitionCommandService.getMonthlyVisitCount(user.getUsersId());

        // 응답 빌드 및 전송
        return UserLoginResponseDTO.builder()
                .token(jwt)
                .isFirstLogin(isFist)
                .name(name)
                .monthlyVisitCount(monthlyVisitCount)
                .build();
    }

    public UserDTO.VerifyPasswordResponseDTO verifyPassword(long uid, String password) {
        User user = userRepository.getUserByUsersId(uid);
        if(user == null) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(password, user.getPw())) {
            throw new GeneralException(ErrorStatus.WRONG_PASSWORD);
        }
        return toUserInfoDTO(true, user);
    }
    UserDTO.VerifyPasswordResponseDTO toUserInfoDTO(boolean verify, User user) {
        var userInfoDTO = UserDTO.UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getName())
                .age(user.getAge())
                .gender(user.getGender().toString())
                .build();
        return UserDTO.VerifyPasswordResponseDTO.builder()
                .verified(verify)
                .userInfo(userInfoDTO)
                .build();

    }

    @Transactional
    public UserDTO.MeBriefResponseDTO getMyBrief(long uid) {
        User user = userRepository.getUserByUsersId(uid);
        if (user == null) throw new GeneralException(ErrorStatus.USER_NOT_FOUND);

        return UserDTO.MeBriefResponseDTO.builder()
                .loginId(user.getId())     // 로그인 아이디
                .nickname(user.getName())  // 닉네임
                .build();
    }

    public void updateLoginId(long uid, String loginId) {
        int updated = userRepository.updateLoginId(uid, loginId);
        if(updated == 0) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
    }

    public void updateNickName(long uid, String nickname) {
        int updated = userRepository.updateNickname(uid, nickname);
        if(updated == 0) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
    }

    public void updatePassWord(long uid, String passWord) {
        int updated = userRepository.updatePassWord(uid, passwordEncoder.encode(passWord));
        if(updated == 0) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
    }
}
