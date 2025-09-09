package com.eyedia.eyedia.global.validation.annotation;

import com.eyedia.eyedia.global.validation.validator.CheckNickNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckNickNameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckNickName {

    String message() default "사용할 수 없는 닉네임 입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}