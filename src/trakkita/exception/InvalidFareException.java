package trakkita.exception;

/** A more specific InvalidInputException for fare / money problems. */
public class InvalidFareException extends InvalidInputException {
    public InvalidFareException(String message) {
        super(message);
    }
}
