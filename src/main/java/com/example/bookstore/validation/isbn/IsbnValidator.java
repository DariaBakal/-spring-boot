package com.example.bookstore.validation.isbn;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {
    private static final String PATTERN_OF_ISBN =
            "^(?=(?:\\\\D*\\\\d){10}(?:(?:\\\\D*\\\\d){3})?$)[\\\\d-]+$";

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext constraintValidatorContext) {
        return isbn != null && Pattern.compile(PATTERN_OF_ISBN).matcher(isbn).matches();
    }
}
