package com.eyedia.eyedia.global.validation.annotation;

import com.eyedia.eyedia.global.validation.validator.CheckLoginIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckLoginIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckLoginId {

    String message() default "사용할 수 없는 로그인 아이디 입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}