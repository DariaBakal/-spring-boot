package com.example.bookstore.validation.isbn;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {
    private static final Pattern ISBN_10 = Pattern.compile("^\\d{9}[\\dX]$");
    private static final Pattern ISBN_13 = Pattern.compile("^\\d{13}$");

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null) {
            return false;
        }

        String cleaned = isbn.replaceAll("[\\s-]", "").toUpperCase();

        if (ISBN_10.matcher(cleaned).matches()) {
            return isValidIsbn10(cleaned);
        } else if (ISBN_13.matcher(cleaned).matches()) {
            return isValidIsbn13(cleaned);
        }
        return false;
    }

    private boolean isValidIsbn10(String isbn) {
        if (isbn.length() != 10) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            char digitChar = isbn.charAt(i);
            if (!Character.isDigit(digitChar)) {
                return false;
            }
            sum += (10 - i) * Character.getNumericValue(digitChar);
        }

        char lastChar = isbn.charAt(9);
        if (lastChar == 'X') {
            sum += 10;
        } else if (Character.isDigit(lastChar)) {
            sum += Character.getNumericValue(lastChar);
        } else {
            return false;
        }

        return sum % 11 == 0;
    }

    private boolean isValidIsbn13(String isbn) {
        if (isbn.length() != 13) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            char digitChar = isbn.charAt(i);
            if (!Character.isDigit(digitChar)) {
                return false;
            }
            int digit = Character.getNumericValue(digitChar);
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        int checkDigit = Character.getNumericValue(isbn.charAt(12));
        int calculatedCheckDigit = (10 - (sum % 10)) % 10;

        return checkDigit == calculatedCheckDigit;
    }
}
