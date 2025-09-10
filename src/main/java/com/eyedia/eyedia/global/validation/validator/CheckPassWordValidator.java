package com.eyedia.eyedia.global.validation.validator;

import com.eyedia.eyedia.dto.UserDTO;
import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.global.validation.annotation.CheckPassWord;
import com.eyedia.eyedia.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckPassWordValidator implements ConstraintValidator<CheckPassWord, Object> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Field로 사용
    private String passValue;
    private String confirmValue;

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String userDetails)) {
            return true;
        }

        Long currentUserId = Long.parseLong(userDetails);

        try {
            if (value instanceof UserDTO.UpdatePassWordRequest dto) {
                passValue = dto.getPassword();
                confirmValue = dto.getConfirmPassword();
            }

            // 비밀번호와 확인비밀번호가 같은 지 확인
            if(!passValue.equals(confirmValue)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(ErrorStatus.WRONG_PASSWORD.name())
                        .addConstraintViolation();
                return false;

            }
            // 이미 같은 비밀번호 일 시
            // 입력한_평문_비밀번호, DB에_저장된_암호화된_비밀번호 순으로
            if(passwordEncoder.matches(passValue, userRepository.getUserByUsersId(currentUserId).getPw())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(ErrorStatus.ALREADY_USER_PASSWORD_SAME.name())
                        .addConstraintViolation();
                return false;

            }

            return true;
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("비밀번호 검증 중 서버 오류가 발생했습니다.")
                    .addConstraintViolation();
            return false;
        }
    }
}