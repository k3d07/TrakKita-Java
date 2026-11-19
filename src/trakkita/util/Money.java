package trakkita.util;

/** Formats pesos. \u20B1 is the peso sign, escaped so any file encoding works. */
public final class Money {
    private Money() { }

    /** Whole pesos only, for tight spaces such as chart labels. */
    public static String pesoShort(double amount) {
        return (amount < 0 ? "-" : "") + "\u20B1" + String.format("%,.0f", Math.abs(amount));
    }

    public static String peso(double amount) {
        String sign = amount < 0 ? "-" : "";
        return sign + "\u20B1" + String.format("%,.2f", Math.abs(amount));
    }
}
