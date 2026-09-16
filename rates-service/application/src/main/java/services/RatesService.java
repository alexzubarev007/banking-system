package services;
import messages.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.*;
@Service
@EnableScheduling
public class RatesService {
    private final QuotePublisher publisher;
    private final Map<String, RatesMessage> quotes = new ConcurrentHashMap<>();
    public RatesService(QuotePublisher publisher) {
        this.publisher = publisher;
        Map.of("USD", "71.15", "EUR", "82.50", "CNY", "10.44").forEach((code, rate) ->
                quotes.put(code, new RatesMessage(code, new BigDecimal(rate), Instant.now())));
    }
    @Scheduled(fixedRateString = "${rates.publish-interval:10s}")
    public void changeRates() {
        quotes.forEach((code, old) -> {
            RatesMessage updated = quotes.compute(code, (key, current) -> new RatesMessage(key,
                    current.rate().add(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(-50, 51), 2))
                            .max(new BigDecimal("0.01")), Instant.now()));
            publisher.publish(updated);
        });
    }
    public RatesResponse getRate(RequestMessage request) {
        RatesMessage quote = request.code() == null ? null : quotes.get(request.code());
        return quote == null ? new RatesResponse(request.code(), null, "Unknown currency", null)
                : new RatesResponse(quote.code(), quote.rate(), "Ok", quote.time());
    }
}
