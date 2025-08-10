package com.example.bookstore.validation.image;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ImageValidator implements ConstraintValidator<Image, String> {
    private static final String PATTERN_OF_IMAGE =
            "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-"
                    + "zA-Z0-9()@:%_\\+.~#?&//=]*)(?:\\.jpg|\\.gif|\\.png|\\.jpeg|\\.webp)$";

    @Override
    public boolean isValid(String imageUrl, ConstraintValidatorContext constraintValidatorContext) {
        return imageUrl == null || Pattern.compile(PATTERN_OF_IMAGE).matcher(imageUrl).matches();
    }
}
