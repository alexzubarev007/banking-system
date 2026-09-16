package messages;
import java.math.BigDecimal;
import java.time.Instant;
public record RatesResponse(String currencyCode, BigDecimal rate, String status, Instant time) { }
