package com.eyedia.eyedia.global.validation.annotation;

import com.eyedia.eyedia.global.validation.validator.CheckPassWordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckPassWordValidator.class)
@Target({ElementType.TYPE}) // 클래스 단위. 단순 타입은 FIELD 사용
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPassWord {

    String message() default "비밀번호를 바꿀 수 없습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // 비교할 필드
    String password();
    String confirmPassword();
}