package io.github.stackpan.tasque.validation.validator;

import io.github.stackpan.tasque.validation.StringOnly;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class StringOnlyValidator implements ConstraintValidator<StringOnly, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(s) || s.isEmpty()) {
            return false;
        }

        if (s.equals("true") || s.equals("false")) {
            return false;
        }

        try {
            Double.parseDouble(s);
            return false;
        } catch (NumberFormatException ignored) {
        }

        return true;
    }
}