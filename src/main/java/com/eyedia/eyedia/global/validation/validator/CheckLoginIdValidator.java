package com.eyedia.eyedia.global.validation.validator;

import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.global.validation.annotation.CheckLoginId;
import com.eyedia.eyedia.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckLoginIdValidator implements ConstraintValidator<CheckLoginId, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // 공백 제거 및 정규화
        String loginId = value.trim();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String userDetails)) {
            return true;
        }

        Long currentUserId = Long.parseLong(userDetails);

        boolean isExists = userRepository.existsByIdAndUsersIdNot(loginId, currentUserId);
        if (isExists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.ALREADY_USER_ID_EXISTS.name())
                    .addConstraintViolation();
            return false;

        }
        boolean isEquals = userRepository.existsByIdAndUsersId(loginId, currentUserId);
        if(isEquals) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.ALREADY_USER_ID_SAME.name())
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
