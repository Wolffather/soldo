package ru.savvy.soldo.annotation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateOrderValidator.class)
@Target({ ElementType.TYPE }) // валидируем класс целиком
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateOrder {
    String message() default "Дата окончания должна быть после даты начала";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
