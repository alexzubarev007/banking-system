package messages;

import java.math.BigDecimal;

public record RatesResponse(String currencyCode,
                            BigDecimal rate,
                            String status) { }
