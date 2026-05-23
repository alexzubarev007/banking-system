package messages;

import java.math.BigDecimal;
import java.time.Instant;

public record RatesMessage(String code,
                           BigDecimal rate,
                           Instant time) { }