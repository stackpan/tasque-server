package io.github.stackpan.tasque.http.validation.validators;

import io.github.stackpan.tasque.http.validation.annotations.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ValidImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        var contentType = value.getContentType();

        assert contentType != null;
        if (!isSupportedContentType(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Only PNG or JPG images are allowed.")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png") || contentType.equals("image/jpeg") || contentType.equals("image/jpg");
    }
}
