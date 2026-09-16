package rates;
import java.math.BigDecimal;
import java.time.Instant;
public record CurrencyQuote(String code, BigDecimal rate, Instant time) {
    public CurrencyQuote {
        if (code == null || !code.matches("[A-Z]{3}") || rate == null || rate.signum() <= 0 || time == null)
            throw new IllegalArgumentException("Invalid currency quote");
    }
}
