package rates;

public class CurrencyProblemException extends RuntimeException {
    public CurrencyProblemException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyProblemException(String message) {
        super(message);
    }
}
