package trakkita.exception;

/** Unchecked: thrown when a user-supplied value fails validation. */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
