package io.github.stackpan.tasque.validation;

import io.github.stackpan.tasque.validation.validator.StringOnlyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StringOnlyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringOnly {
    String message() default "Must be a valid string";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}