package trakkita.exception;

/** Checked: thrown when the data file cannot be read or contains a bad line. */
public class DataCorruptedException extends Exception {
    private final int lineNumber;

    public DataCorruptedException(String message, int lineNumber, Throwable cause) {
        super(message, cause);
        this.lineNumber = lineNumber;
    }

    public DataCorruptedException(String message, int lineNumber) {
        this(message, lineNumber, null);
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
