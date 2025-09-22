package com.eyedia.eyedia.global.validation.validator;

import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.global.validation.annotation.CheckNickName;
import com.eyedia.eyedia.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckNickNameValidator implements ConstraintValidator<CheckNickName, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 닉네임이 선택 입력이면: null/blank 허용
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        // 공백 제거 및 정규화
        String nickname = value.trim();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String userDetails)) {
            return true;
        }

        Long currentUserId = Long.parseLong(userDetails);

        boolean isExists = userRepository.existsByNameAndUsersIdNot(nickname, currentUserId);
        if (isExists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.ALREADY_USER_NAME_EXISTS.name())
                    .addConstraintViolation();
            return false;
        }
        boolean isEquals = userRepository.existsByNameAndUsersId(nickname, currentUserId);
        if(isEquals) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.ALREADY_USER_NAME_SAME.name())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
