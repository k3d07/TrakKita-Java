package trakkita.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import trakkita.exception.InvalidInputException;

/** Central place for input parsing and validation (used by GUI and models). */
public final class Validator {
    private Validator() { }

    public static double parseNumber(String text, String field) {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidInputException(field + " is required.");
        }
        try {
            double v = Double.parseDouble(text.trim().replace(",", ""));
            if (Double.isNaN(v) || Double.isInfinite(v)) {
                throw new NumberFormatException();
            }
            return v;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(field + " must be a number, such as 12.50.");
        }
    }

    public static int parseWhole(String text, String field) {
        double v = parseNumber(text, field);
        if (v != Math.floor(v)) {
            throw new InvalidInputException(field + " must be a whole number.");
        }
        return (int) v;
    }

    public static LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidInputException("Date is required.");
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Date must look like 2026-11-18 (yyyy-MM-dd).");
        }
    }

    public static double requireNonNegative(double v, String field) {
        if (v < 0) {
            throw new InvalidInputException(field + " cannot be negative.");
        }
        return v;
    }

    public static double requirePositive(double v, String field) {
        if (v <= 0) {
            throw new InvalidInputException(field + " must be greater than zero.");
        }
        return v;
    }
}
