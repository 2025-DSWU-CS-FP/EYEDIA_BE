package com.eyedia.eyedia.global.validation.validator;

import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.global.validation.annotation.CheckPassWord;
import com.eyedia.eyedia.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;

@Component
@RequiredArgsConstructor
public class CheckPassWordValidator implements ConstraintValidator<CheckPassWord, Object> {
    private final UserRepository userRepository;

    // Field로 사용
    private String passwordField;
    private String confirmPasswordField;
    // 초기화하여 변수 여러개를 받아와야 함
    @Override
    public void initialize(CheckPassWord constraintAnnotation) {
        this.passwordField = constraintAnnotation.password();
        this.confirmPasswordField = constraintAnnotation.confirmPassword();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String userDetails)) {
            return true;
        }

        Long currentUserId = Long.parseLong(userDetails);

        try {
            Field password = value.getClass().getDeclaredField(passwordField);
            Field confirmPassword = value.getClass().getDeclaredField(confirmPasswordField);

            password.setAccessible(true);
            confirmPassword.setAccessible(true);

            // dto 값을 꺼내온다
            Object passValue = password.get(value);
            Object confirmValue = confirmPassword.get(value);

            if(!password.equals(confirmPassword)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(ErrorStatus.WRONG_PASSWORD.getMessage())
                        .addConstraintViolation();
                return false;

            }
            // 이미 같은 비밀번호 일 시
            if(userRepository.getUserByUsersId(currentUserId).equals(passwordField)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(ErrorStatus.ALREADY_USER_PASSWORD_SAME.getMessage())
                        .addConstraintViolation();
                return false;

            }

            return passValue.equals(confirmValue);
        } catch (Exception e) {
            return false; // 필드 못 찾거나 예외 발생 시 invalid
        }
    }
}
